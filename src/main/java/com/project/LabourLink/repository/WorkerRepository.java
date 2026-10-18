package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findBySkillsContainingIgnoreCaseAndLocationIgnoreCaseAndAvailableTrue(
            String skills, String location);

    Optional<Worker> findByEmail(String email);

    boolean existsByEmail(String email);
}