package com.project.LabourLink.service;

import com.project.LabourLink.entity.Notification;
import com.project.LabourLink.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Create and save a notification
    public Notification createNotification(
            String recipientEmail, String message) {

        Notification notification = new Notification(recipientEmail, message);

        return notificationRepository.save(notification);
    }

    // Get all notifications for a user
    public List<Notification> getNotifications(String recipientEmail) {
        return notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(recipientEmail);
    }

    // Count unread notifications
    public long getUnreadCount(String recipientEmail) {
        return notificationRepository
                .countByRecipientEmailAndIsReadFalse(recipientEmail);
    }

    // Mark a notification as read only if it belongs to the user
    @Transactional
    public boolean markAsRead(Long notificationId, String recipientEmail) {

        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.getRecipientEmail()
                        .equalsIgnoreCase(recipientEmail))
                .map(notification -> {
                    notification.setRead(true);
                    return true;
                })
                .orElse(false);
    }

    // Mark all notifications as read for a user
    @Transactional
    public void markAllAsRead(String recipientEmail) {

        List<Notification> notifications = notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(
                        recipientEmail);

        notifications.forEach(notification -> notification.setRead(true));
    }
}
