
package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.service.WorkerService;
import com.project.LabourLink.service.NotificationService;
import com.project.LabourLink.service.ApplicationService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final WorkerService workerService;
    private final NotificationService notificationService;
    private final ApplicationService applicationService;

    public DashboardController(
            WorkerService workerService,
            NotificationService notificationService,
            ApplicationService applicationService) {
        this.workerService = workerService;
        this.notificationService = notificationService;
        this.applicationService = applicationService;
    }

    @GetMapping("/worker/dashboard")
    public String workerDashboard(
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        model.addAttribute("email", email);

        Worker worker = workerService.getAllWorkers()
                .stream()
                .filter(w -> w.getEmail() != null
                        && w.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (worker == null) {
            return "redirect:/login";
        }

        model.addAttribute("workerId", worker.getId());

        // Get the worker's total application count
        long applicationCount = applicationService.getAllApplications()
                .stream()
                .filter(app -> app.getWorker() != null
                        && app.getWorker().getId().equals(worker.getId()))
                .count();

        model.addAttribute("applicationCount", applicationCount);

        // Get the worker's unread notification count
        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(email));

        return "worker-dashboard";
    }

    @GetMapping("/employer/dashboard")
    public String employerDashboard(
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        model.addAttribute("email", email);

        // Get the employer's unread notification count
        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(email));

        return "employer-dashboard";
    }

    @GetMapping("/worker/profile")
    public String workerProfile(
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        Worker worker = workerService.getAllWorkers()
                .stream()
                .filter(w -> w.getEmail() != null
                        && w.getEmail().equalsIgnoreCase(
                                authentication.getName()))
                .findFirst()
                .orElse(null);

        if (worker == null) {
            return "redirect:/worker/dashboard";
        }

        model.addAttribute("worker", worker);

        return "worker-profile";
    }
}
