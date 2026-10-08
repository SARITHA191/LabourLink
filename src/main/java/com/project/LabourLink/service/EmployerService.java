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

    public Employer saveEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }
}