package com.project.LabourLink.config;

import com.project.LabourLink.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomUserDetailsService userDetailsService)
            throws Exception {

        http
                .userDetailsService(userDetailsService)

                .authorizeHttpRequests(authorize -> authorize

                        // Public pages
                        .requestMatchers(
                                "/",
                                "/login",
                                "/workers/register",
                                "/employers/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error")
                        .permitAll()

                        // Worker-only dashboard and profile
                        .requestMatchers(
                                "/worker/**")
                        .hasRole("WORKER")

                        // Employer-only dashboard
                        .requestMatchers(
                                "/employer/**")
                        .hasRole("EMPLOYER")

                        // Only employers can view the complete worker list
                        .requestMatchers(
                                "/workers")
                        .hasRole("EMPLOYER")

                        // Workers can access their matching jobs and applications
                        .requestMatchers(
                                "/workers/{id}/jobs",
                                "/workers/{id}/applications")
                        .hasRole("WORKER")

                        // All other routes require login
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")

                        .successHandler((request, response, authentication) -> {

                            boolean isWorker = authentication.getAuthorities()
                                    .stream()
                                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_WORKER"));

                            if (isWorker) {
                                response.sendRedirect("/worker/dashboard");
                            } else {
                                response.sendRedirect("/employer/dashboard");
                            }
                        })

                        .failureUrl("/login?error=true")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll());

        // CSRF protection remains enabled by default.
        return http.build();
    }
}