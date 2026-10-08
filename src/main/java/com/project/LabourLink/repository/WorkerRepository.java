package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

}