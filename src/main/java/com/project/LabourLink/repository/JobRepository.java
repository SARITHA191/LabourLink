package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByWorkTypeContainingIgnoreCaseAndLocationIgnoreCaseAndStatus(
            String workType,
            String location,
            String status);
}