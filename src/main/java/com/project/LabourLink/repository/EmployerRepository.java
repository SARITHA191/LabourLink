package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository
        extends JpaRepository<Employer, Long> {

    Optional<Employer> findByEmail(String email);

    boolean existsByEmail(String email);
}