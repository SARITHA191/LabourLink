package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
