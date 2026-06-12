package com.job_board.job_sevice.respository;

import com.job_board.job_sevice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepo extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {
}
