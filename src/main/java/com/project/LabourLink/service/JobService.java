
package com.project.LabourLink.service;

import com.project.LabourLink.entity.Job;
import com.project.LabourLink.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with ID: " + id));
    }

    // Find open jobs matching at least one worker skill and location
    public List<Job> findMatchingJobs(
            String workerSkills,
            String workerLocation) {

        if (workerSkills == null || workerLocation == null
                || workerSkills.isBlank()
                || workerLocation.isBlank()) {
            return List.of();
        }

        List<String> skills = Arrays.stream(
                workerSkills.split("[,;]"))
                .map(skill -> skill.trim().toLowerCase(Locale.ROOT))
                .filter(skill -> !skill.isBlank())
                .toList();

        return jobRepository.findAll()
                .stream()
                .filter(job -> job.getStatus() != null
                        && job.getStatus().equalsIgnoreCase("OPEN"))
                .filter(job -> job.getLocation() != null
                        && job.getLocation().trim()
                                .equalsIgnoreCase(workerLocation.trim()))
                .filter(job -> job.getWorkType() != null
                        && skills.stream().anyMatch(skill -> job.getWorkType().toLowerCase(Locale.ROOT)
                                .contains(skill)))
                .toList();
    }
}
