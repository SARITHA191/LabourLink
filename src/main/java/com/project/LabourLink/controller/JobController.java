package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.entity.Job;
import com.project.LabourLink.service.EmployerService;
import com.project.LabourLink.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final EmployerService employerService;

    public JobController(JobService jobService,
            EmployerService employerService) {
        this.jobService = jobService;
        this.employerService = employerService;
    }

    @GetMapping("/post")
    public String showJobForm(Model model) {

        model.addAttribute("job", new Job());

        model.addAttribute("employers",
                employerService.getAllEmployers());

        return "job-post";
    }

    @PostMapping("/post")
    public String postJob(
            @ModelAttribute("job") Job job,
            @RequestParam("employerId") Long employerId) {

        Employer employer = employerService.getEmployerById(employerId);

        job.setEmployer(employer);

        jobService.saveJob(job);

        return "redirect:/jobs";
    }

    @GetMapping
    public String getAllJobs(Model model) {

        model.addAttribute("jobs",
                jobService.getAllJobs());

        return "jobs";
    }
}