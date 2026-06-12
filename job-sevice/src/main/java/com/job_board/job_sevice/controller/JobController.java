package com.job_board.job_sevice.controller;

import com.job_board.job_sevice.Dtos.JobDto;
import com.job_board.job_sevice.entity.Job;
import com.job_board.job_sevice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Job> postJob(
            @RequestBody JobDto dto,
            Authentication authentication  // ← Spring injects this automatically
    ) {
        // get employerId from the JWT token — NOT from request params
        // this was set in JwtAuthFilter as credentials
        Long employerId = (Long) authentication.getCredentials();

        // get email from token as well
        String employerEmail = authentication.getName();

        Job job = jobService.postJob(dto, employerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    @GetMapping
    public ResponseEntity<Page<Job>> searchJobs(

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            Double minSalary,

            @RequestParam(required = false)
            Double maxSalary,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                jobService.searchJobs(
                        title,
                        location,
                        minSalary,
                        maxSalary,
                        pageable
                )
        );
    }

    @PostMapping("/{jobId}/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<String> applyToJob(

            @PathVariable Long jobId,

            @RequestParam Long candidateId,

            @RequestParam String candidateEmail
    ) {

        jobService.applyToJob(
                jobId,
                candidateId,
                candidateEmail
        );

        return ResponseEntity.ok(
                "Application submitted successfully"
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Job> getJob(
            @PathVariable Long jobId
    ) {

        Job job = jobService.getJob(jobId);

        return ResponseEntity.ok(job);
    }
}