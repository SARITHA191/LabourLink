package com.project.LabourLink.controller;

import com.project.LabourLink.entity.Notification;
import com.project.LabourLink.service.NotificationService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Display notifications for the logged-in user
    @GetMapping
    public String showNotifications(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        List<Notification> notifications = notificationService.getNotifications(email);

        model.addAttribute("notifications", notifications);
        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(email));

        return "notifications";
    }

    // Mark one notification as read
    @PostMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        notificationService.markAsRead(
                id, authentication.getName());

        return "redirect:/notifications";
    }

    // Mark all notifications as read
    @PostMapping("/read-all")
    public String markAllAsRead(
            Authentication authentication) {

        notificationService.markAllAsRead(
                authentication.getName());

        return "redirect:/notifications";
    }
}