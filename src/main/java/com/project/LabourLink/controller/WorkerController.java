package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.service.WorkerService;
import com.project.LabourLink.service.JobService;
import com.project.LabourLink.service.ApplicationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WorkerController {

    private final WorkerService workerService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final PasswordEncoder passwordEncoder;

    public WorkerController(
            WorkerService workerService,
            JobService jobService,
            ApplicationService applicationService,
            PasswordEncoder passwordEncoder) {

        this.workerService = workerService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.passwordEncoder = passwordEncoder;
    }

    // Show worker registration page
    @GetMapping("/workers/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("worker", new Worker());
        return "worker-register";
    }

    // Register a new worker
    @PostMapping("/workers/register")
    public String registerWorker(
            @ModelAttribute Worker worker) {

        String encodedPassword = passwordEncoder.encode(worker.getPassword());

        worker.setPassword(encodedPassword);

        workerService.saveWorker(worker);

        return "redirect:/";
    }

    // Display all workers
    @GetMapping("/workers")
    public String getAllWorkers(Model model) {
        model.addAttribute(
                "workers",
                workerService.getAllWorkers());

        return "workers";
    }

    // Find jobs matching a worker's skills and location
    @GetMapping("/workers/{id}/jobs")
    public String getMatchingJobs(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Worker worker = workerService.getWorkerById(id);

        if (worker == null) {
            return "redirect:/worker/dashboard";
        }

        // Only the owner of this worker account can access it
        if (!worker.getEmail().equalsIgnoreCase(
                authentication.getName())) {

            throw new AccessDeniedException(
                    "You cannot access another worker's jobs.");
        }

        model.addAttribute("worker", worker);

        model.addAttribute(
                "jobs",
                jobService.findMatchingJobs(
                        worker.getSkills(),
                        worker.getLocation()));

        return "worker-jobs";
    }

    // Display application history belonging to the logged-in worker
    @GetMapping("/workers/{id}/applications")
    public String getWorkerApplications(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Worker worker = workerService.getWorkerById(id);

        if (worker == null) {
            return "redirect:/worker/dashboard";
        }

        // Verify account ownership
        if (!worker.getEmail().equalsIgnoreCase(
                authentication.getName())) {

            throw new AccessDeniedException(
                    "You cannot access another worker's applications.");
        }

        model.addAttribute("worker", worker);

        model.addAttribute(
                "applications",
                applicationService.getAllApplications()
                        .stream()
                        .filter(app -> app.getWorker().getId().equals(id))
                        .toList());

        return "worker-applications";
    }

    // Allow employers to view an individual worker's profile
    @GetMapping("/workers/{id}/profile")
    public String viewWorkerProfile(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Worker worker = workerService.getWorkerById(id);

        if (worker == null) {
            return "redirect:/workers";
        }

        // Only employers should use this profile page
        boolean isEmployer = authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_EMPLOYER"));

        if (!isEmployer) {
            throw new AccessDeniedException(
                    "Only employers can view worker profiles.");
        }

        model.addAttribute("worker", worker);

        return "employer-worker-profile";
    }

}