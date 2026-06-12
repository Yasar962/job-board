package com.job_board.ai_sevice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private int matchScore; // 0 to 100
    private List matchedSkills;
    private List missingSkills;
    private String summary; // one line explanation
}