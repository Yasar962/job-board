package com.job_board.job_sevice.service;

import com.job_board.job_sevice.Dtos.JobDto;
import com.job_board.job_sevice.Dtos.UserDto;
import com.job_board.job_sevice.RabbitMQConfig;
import com.job_board.job_sevice.client.UserServiceClient;
import com.job_board.job_sevice.entity.Application;
import com.job_board.job_sevice.entity.ApplicationCreatedEvent;
import com.job_board.job_sevice.entity.Job;
import com.job_board.job_sevice.respository.ApplicationRepo;
import com.job_board.job_sevice.respository.JobRepo;
import com.job_board.job_sevice.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.AprLifecycleListener;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepo jobRepo;
    private final UserServiceClient userServiceClient;
    private final ApplicationRepo applicationRepo;
    private final AmqpTemplate amqpTemplate;

    @CacheEvict(value = "jobSearch", allEntries = true)
    public Job postJob(JobDto dto, Long employerId){
        Job job = Job.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .maxSalary(dto.getMaxSalary())
                .minSalary(dto.getMinSalary())
                .employerId(employerId)
                .build();

        Job saved = jobRepo.save(job);
        return saved;
    }

    public void applyToJob(
            Long jobId,
            Long candidateId,
            String candidateEmail
    ) {

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // Verify candidate exists
        UserDto candidate =
                userServiceClient.getUserById(candidateId);

        if (candidate == null) {
            throw new RuntimeException("Candidate not found");
        }

        boolean alreadyApplied =
                applicationRepo
                        .existsByJobIdAndCandidateId(
                                jobId,
                                candidateId
                        );

        if (alreadyApplied) {
            throw new RuntimeException(
                    "You have already applied for this job"
            );
        }

        Application application =
                Application.builder()
                        .jobId(jobId)
                        .candidateId(candidateId)
                        .candidateEmail(candidateEmail)
                        .build();

        Application savedApplication =
                applicationRepo.save(application);

        ApplicationCreatedEvent event =
                new ApplicationCreatedEvent(
                        savedApplication.getId(),
                        job.getId(),
                        job.getTitle(),
                        job.getCompanyName(),
                        candidateId,
                        candidateEmail,
                        savedApplication.getAppliedAt()
                );

        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }

    @Cacheable(value = "jobSearch", key = "#title + '-' + #location + '-' + #minSalary + '-' + #maxSalary + '-' + #pageable.pageNumber")
    public Page<Job> searchJobs(
            String title,
            String location,
            Double minSalary,
            Double maxSalary,
            Pageable pageable
    ) {

        Specification<Job> specification =
                Specification.where(
                                JobSpecification.hasTitle(title)
                        )
                        .and(
                                JobSpecification.hasLocation(location)
                        )
                        .and(
                                JobSpecification.salaryBetween(
                                        minSalary,
                                        maxSalary
                                )
                        );

        return jobRepo.findAll(
                specification,
                pageable
        );
    }

    public Job getJob(Long jobId) {

        return jobRepo.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));
    }
}
