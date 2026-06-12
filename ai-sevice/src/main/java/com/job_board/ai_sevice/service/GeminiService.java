package com.job_board.ai_sevice.service;

import com.job_board.ai_sevice.dtos.MatchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    public MatchResponse analyzeMatch(String resumeText, String jobDescription) {
        String prompt = buildPrompt(resumeText, jobDescription);
// build request body
        Map part = Map.of("text", prompt);
        Map content = Map.of("parts", List.of(part));
        Map body = Map.of("contents", List.of(content));
        String url = apiUrl + "?key=" + apiKey;
        Map response = restTemplate.postForObject(url, body, Map.class);
// extract text from response and parse JSON
        String text = extractTextFromResponse(response);
        return parseMatchResponse(text);
    }

    private String buildPrompt(String resume, String jd) {
        return """
Analyze this resume against this job description.
Return ONLY a valid JSON object with no extra text, no markdown, no explanation.
JSON format must be exactly:
{
\"matchScore\": ,
\"matchedSkills\": [\"skill1\", \"skill2\"],
\"missingSkills\": [\"skill1\", \"skill2\"],
\"summary\": \"one sentence summary\"
}","
Resume: """ + resume + """
Job Description: """ + jd;
    }

    private String extractTextFromResponse(Map response) {
        List candidates = (List) response.get("candidates");
        Map candidate = (Map) candidates.get(0);
        Map content = (Map) candidate.get("content");
        List parts = (List) content.get("parts");
        Map part = (Map) parts.get(0);
        return (String) part.get("text");
    }

    private MatchResponse parseMatchResponse(String jsonText) {
        try {
            String clean = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();
            return new ObjectMapper().readValue(clean, MatchResponse.class);
        } catch (Exception e) {
            return new MatchResponse(0, List.of(), List.of(), "Could not parse response");
        }
    }
}