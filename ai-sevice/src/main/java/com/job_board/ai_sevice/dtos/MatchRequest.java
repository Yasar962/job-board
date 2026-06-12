package com.job_board.ai_sevice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequest {
    String resumeText;
    String jobDescription;
}
