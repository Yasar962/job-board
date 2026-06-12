package com.job_board.job_sevice.respository;

import com.job_board.job_sevice.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepo extends JpaRepository<Application,Long> {
    boolean existsByJobIdAndCandidateId(Long jobId,Long candidateId);
}
