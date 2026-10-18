
package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Application;
import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.entity.Job;
import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.service.ApplicationService;
import com.project.LabourLink.service.EmployerService;
import com.project.LabourLink.service.JobService;
import com.project.LabourLink.service.WorkerService;
import com.project.LabourLink.service.NotificationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final WorkerService workerService;
    private final JobService jobService;
    private final EmployerService employerService;
    private final NotificationService notificationService;

    public ApplicationController(
            ApplicationService applicationService,
            WorkerService workerService,
            JobService jobService,
            EmployerService employerService,
            NotificationService notificationService) {

        this.applicationService = applicationService;
        this.workerService = workerService;
        this.jobService = jobService;
        this.employerService = employerService;
        this.notificationService = notificationService;
    }

    // Worker applies for a job
    @PostMapping("/apply")
    public String applyForJob(
            @RequestParam("jobId") Long jobId,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        Worker worker = workerService.getAllWorkers().stream()
                .filter(w -> w.getEmail() != null
                        && w.getEmail().equalsIgnoreCase(
                                authentication.getName()))
                .findFirst()
                .orElse(null);

        if (worker == null) {
            throw new AccessDeniedException(
                    "Only registered workers can apply for jobs.");
        }

        Job job = jobService.getJobById(jobId);

        if (job == null) {
            return "redirect:/jobs?notFound=true";
        }

        if (job.getStatus() == null
                || !job.getStatus().equalsIgnoreCase("OPEN")) {
            return "redirect:/jobs?closed=true";
        }

        if (applicationService.hasAlreadyApplied(
                worker.getId(), jobId)) {
            return "redirect:/jobs?duplicate=true";
        }

        Application application = new Application();
        application.setWorker(worker);
        application.setJob(job);
        application.setStatus("PENDING");

        applicationService.saveApplication(application);

        // Notify the employer about the new application
        Employer employer = job.getEmployer();

        if (employer != null && employer.getEmail() != null) {
            notificationService.createNotification(
                    employer.getEmail(),
                    worker.getName() + " applied for your "
                            + job.getWorkType() + " job.");
        }

        return "redirect:/jobs?success=true";
    }

    // Accept an application
    @PostMapping("/{id}/accept")
    public String acceptApplication(
            @PathVariable Long id,
            Authentication authentication) {

        Application application = getApplication(id);

        verifyEmployerOwnsJob(application, authentication);

        applicationService.updateApplicationStatus(id, "ACCEPTED");

        // Notify the worker
        Worker worker = application.getWorker();

        if (worker != null && worker.getEmail() != null) {
            notificationService.createNotification(
                    worker.getEmail(),
                    "Your application for "
                            + application.getJob().getWorkType()
                            + " has been accepted.");
        }

        return "redirect:/jobs/"
                + application.getJob().getId() + "/applicants";
    }

    // Reject an application
    @PostMapping("/{id}/reject")
    public String rejectApplication(
            @PathVariable Long id,
            Authentication authentication) {

        Application application = getApplication(id);

        verifyEmployerOwnsJob(application, authentication);

        applicationService.updateApplicationStatus(id, "REJECTED");

        // Notify the worker
        Worker worker = application.getWorker();

        if (worker != null && worker.getEmail() != null) {
            notificationService.createNotification(
                    worker.getEmail(),
                    "Your application for "
                            + application.getJob().getWorkType()
                            + " was not accepted this time.");
        }

        return "redirect:/jobs/"
                + application.getJob().getId() + "/applicants";
    }

    private Application getApplication(Long id) {
        return applicationService.getAllApplications().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Application not found: " + id));
    }

    private void verifyEmployerOwnsJob(
            Application application,
            Authentication authentication) {

        if (authentication == null) {
            throw new AccessDeniedException("Please log in.");
        }

        Employer employer = employerService.getAllEmployers().stream()
                .filter(e -> e.getEmail() != null
                        && e.getEmail().equalsIgnoreCase(
                                authentication.getName()))
                .findFirst()
                .orElse(null);

        if (employer == null
                || application.getJob().getEmployer() == null
                || !application.getJob().getEmployer().getId()
                        .equals(employer.getId())) {
            throw new AccessDeniedException(
                    "You cannot manage applications for this job.");
        }
    }
}
