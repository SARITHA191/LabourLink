package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    // Get applications for a particular job
    List<Application> findByJobId(Long jobId);

    // Check whether a worker has already applied for a job
    boolean existsByWorkerIdAndJobId(Long workerId, Long jobId);
}