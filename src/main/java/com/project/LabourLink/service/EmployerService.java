package com.project.LabourLink.service;

import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.repository.EmployerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    // Save a new employer
    public Employer saveEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    // Get all employers
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    // Get employer by ID
    public Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with ID: " + id));
    }
}