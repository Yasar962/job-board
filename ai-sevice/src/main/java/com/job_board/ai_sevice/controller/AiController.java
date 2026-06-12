package com.job_board.ai_sevice.controller;

import com.job_board.ai_sevice.dtos.MatchRequest;
import com.job_board.ai_sevice.dtos.MatchResponse;
import com.job_board.ai_sevice.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private GeminiService geminiService;
    @PostMapping("/match")
    public ResponseEntity matchResumeToJob(@RequestBody MatchRequest request) {
        if (request.getResumeText() == null && request.getJobDescription() == null) {
            return ResponseEntity.badRequest().build();
        }
        MatchResponse response = geminiService.analyzeMatch(
                request.getResumeText(), request.getJobDescription());
        return ResponseEntity.ok(response);
    }
}