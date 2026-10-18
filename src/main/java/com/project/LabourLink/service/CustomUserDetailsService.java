package com.project.LabourLink.service;

import com.project.LabourLink.entity.Worker;
import com.project.LabourLink.entity.Employer;
import com.project.LabourLink.repository.WorkerRepository;
import com.project.LabourLink.repository.EmployerRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final WorkerRepository workerRepository;
    private final EmployerRepository employerRepository;

    public CustomUserDetailsService(
            WorkerRepository workerRepository,
            EmployerRepository employerRepository) {

        this.workerRepository = workerRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Worker worker = workerRepository.findByEmail(email)
                .orElse(null);

        if (worker != null) {
            return User.withUsername(worker.getEmail())
                    .password(worker.getPassword())
                    .roles("WORKER")
                    .build();
        }

        Employer employer = employerRepository.findByEmail(email)
                .orElse(null);

        if (employer != null) {
            return User.withUsername(employer.getEmail())
                    .password(employer.getPassword())
                    .roles("EMPLOYER")
                    .build();
        }

        throw new UsernameNotFoundException(
                "No account found with email: " + email);
    }
}