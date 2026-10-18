package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.service.EmployerService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EmployerController {

    private final EmployerService employerService;
    private final PasswordEncoder passwordEncoder;

    public EmployerController(
            EmployerService employerService,
            PasswordEncoder passwordEncoder) {
        this.employerService = employerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/employers/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("employer", new Employer());
        return "employer-register";
    }

    @PostMapping("/employers/register")
    public String registerEmployer(
            @ModelAttribute Employer employer) {

        String encodedPassword = passwordEncoder.encode(employer.getPassword());

        employer.setPassword(encodedPassword);

        employerService.saveEmployer(employer);

        return "redirect:/";
    }
}