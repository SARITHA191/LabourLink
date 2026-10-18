package com.project.LabourLink.service;

import com.project.LabourLink.entity.Application;
import com.project.LabourLink.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // Save a new application
    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    // Get all applications
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get applications for a particular job
    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // Accept or reject an application
    public Application updateApplicationStatus(Long id, String status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Application not found with ID: " + id));

        application.setStatus(status);
        return applicationRepository.save(application);
    }

    // Check whether the worker has already applied
    public boolean hasAlreadyApplied(Long workerId, Long jobId) {
        return applicationRepository
                .existsByWorkerIdAndJobId(workerId, jobId);
    }
}