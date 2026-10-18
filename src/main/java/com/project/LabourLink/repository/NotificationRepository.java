package com.project.LabourLink.repository;

import com.project.LabourLink.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(
            String recipientEmail);

    long countByRecipientEmailAndIsReadFalse(
            String recipientEmail);
}