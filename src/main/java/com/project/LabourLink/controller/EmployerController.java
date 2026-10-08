package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.service.EmployerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employers")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("employer", new Employer());

        return "employer-register";
    }

    @PostMapping("/register")
    public String registerEmployer(
            @ModelAttribute("employer") Employer employer) {

        employerService.saveEmployer(employer);

        return "redirect:/";
    }
}
