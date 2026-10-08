package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.service.WorkerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("worker", new Worker());

        return "worker-register";
    }

    @PostMapping("/register")
    public String registerWorker(@ModelAttribute("worker") Worker worker) {

        workerService.saveWorker(worker);

        return "redirect:/";
    }

    @GetMapping
    public String getAllWorkers(Model model) {

        List<Worker> workers = workerService.getAllWorkers();

        model.addAttribute("workers", workers);

        return "workers";
    }
}