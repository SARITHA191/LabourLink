
package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Application;
import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.entity.Job;
import com.project.LabourLink.service.ApplicationService;
import com.project.LabourLink.service.EmployerService;
import com.project.LabourLink.service.JobService;
import com.project.LabourLink.service.WorkerService;
import com.project.LabourLink.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final EmployerService employerService;
    private final WorkerService workerService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    public JobController(
            JobService jobService,
            EmployerService employerService,
            WorkerService workerService,
            ApplicationService applicationService,
            NotificationService notificationService) {
        this.jobService = jobService;
        this.employerService = employerService;
        this.workerService = workerService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    // Find the currently logged-in employer
    private Employer getLoggedInEmployer(
            Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        return employerService.getAllEmployers()
                .stream()
                .filter(employer -> employer.getEmail() != null
                        && employer.getEmail().equalsIgnoreCase(
                                authentication.getName()))
                .findFirst()
                .orElse(null);
    }

    // Show the job-posting form
    @GetMapping("/post")
    public String showJobForm(
            Model model,
            Authentication authentication) {

        Employer employer = getLoggedInEmployer(authentication);

        if (employer == null) {
            throw new AccessDeniedException(
                    "Only registered employers can post jobs.");
        }

        model.addAttribute("job", new Job());
        model.addAttribute("employerId", employer.getId());

        return "job-post";
    }

    // Save a new job
    @PostMapping("/post")
    public String postJob(
            @ModelAttribute("job") Job job,
            Authentication authentication) {

        Employer employer = getLoggedInEmployer(authentication);

        if (employer == null) {
            throw new AccessDeniedException(
                    "Only registered employers can post jobs.");
        }

        job.setEmployer(employer);

        // New jobs are open by default
        job.setStatus("OPEN");

        jobService.saveJob(job);

        return "redirect:/jobs?posted=true";
    }

    // Display jobs and application-result messages
    @GetMapping
    public String getAllJobs(
            Model model,
            Authentication authentication,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String duplicate,
            @RequestParam(required = false) String closed,
            @RequestParam(required = false) String notFound,
            @RequestParam(required = false) String posted,
            @RequestParam(required = false) String accessDenied) {

        boolean isEmployer = authentication != null
                && authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority -> authority.getAuthority()
                                .equals("ROLE_EMPLOYER"));

        List<Job> jobs;

        if (isEmployer) {
            Employer employer = getLoggedInEmployer(authentication);

            if (employer == null) {
                throw new AccessDeniedException(
                        "Employer account could not be found.");
            }

            // Employers see only their own jobs
            jobs = jobService.getAllJobs()
                    .stream()
                    .filter(job -> job.getEmployer() != null
                            && job.getEmployer().getId()
                                    .equals(employer.getId()))
                    .toList();

        } else {
            // Workers can see all jobs
            jobs = jobService.getAllJobs();

            // Pass the logged-in worker's ID to the page
            if (authentication != null) {
                workerService.getAllWorkers()
                        .stream()
                        .filter(worker -> worker.getEmail() != null
                                && worker.getEmail().equalsIgnoreCase(
                                        authentication.getName()))
                        .findFirst()
                        .ifPresent(worker -> model.addAttribute(
                                "workerId",
                                worker.getId()));
            }
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("isEmployer", isEmployer);

        model.addAttribute(
                "applicationSuccess", success != null);
        model.addAttribute(
                "applicationDuplicate", duplicate != null);
        model.addAttribute(
                "applicationClosed", closed != null);
        model.addAttribute(
                "jobNotFound", notFound != null);
        model.addAttribute(
                "jobPosted", posted != null);
        model.addAttribute(
                "accessDenied", accessDenied != null);

        return "jobs";
    }

    // View applicants for an employer's own job
    @GetMapping("/{id}/applicants")
    public String viewApplicants(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Job job = jobService.getJobById(id);

        if (job == null) {
            return "redirect:/jobs?notFound=true";
        }

        Employer employer = getLoggedInEmployer(authentication);

        if (employer == null
                || job.getEmployer() == null
                || !job.getEmployer().getId()
                        .equals(employer.getId())) {

            throw new AccessDeniedException(
                    "You cannot view applicants for this job.");
        }

        List<Application> applications = applicationService.getApplicationsByJobId(id);

        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        return "job-applicants";
    }

    // View matching workers for an employer's own job
    @GetMapping("/{id}/matches")
    public String viewMatchingWorkers(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Job job = jobService.getJobById(id);

        if (job == null) {
            return "redirect:/jobs?notFound=true";
        }

        Employer employer = getLoggedInEmployer(authentication);

        if (employer == null
                || job.getEmployer() == null
                || !job.getEmployer().getId()
                        .equals(employer.getId())) {

            throw new AccessDeniedException(
                    "You cannot view matching workers for this job.");
        }

        model.addAttribute("job", job);

        model.addAttribute(
                "workers",
                workerService.findMatchingWorkers(
                        job.getWorkType(),
                        job.getLocation()));

        return "matching-workers";
    }

    // Mark an employer's own job as completed

    @PostMapping("/{id}/complete")
    public String completeJob(
            @PathVariable Long id,
            Authentication authentication) {

        Job job = jobService.getJobById(id);

        if (job == null) {
            return "redirect:/jobs?notFound=true";
        }

        Employer employer = getLoggedInEmployer(authentication);

        // Verify job ownership
        if (employer == null
                || job.getEmployer() == null
                || !job.getEmployer().getId()
                        .equals(employer.getId())) {

            throw new AccessDeniedException(
                    "You cannot complete this job.");
        }

        // Prevent duplicate notifications
        if ("COMPLETED".equalsIgnoreCase(job.getStatus())) {
            return "redirect:/jobs";
        }

        // Mark the job as completed
        job.setStatus("COMPLETED");
        jobService.saveJob(job);

        // Notify workers who applied
        List<Application> applications = applicationService.getApplicationsByJobId(id);

        for (Application application : applications) {
            if (application.getWorker() != null
                    && application.getWorker().getEmail() != null) {

                notificationService.createNotification(
                        application.getWorker().getEmail(),
                        "The job '" + job.getWorkType()
                                + "' in " + job.getLocation()
                                + " has been marked as completed.");
            }
        }

        return "redirect:/jobs";
    }

}
