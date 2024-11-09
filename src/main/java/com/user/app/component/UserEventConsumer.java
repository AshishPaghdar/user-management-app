package com.user.app.component;

import com.user.app.entity.AuditLog;
import com.user.app.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserEventConsumer {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @KafkaListener(topics = "user-events", groupId = "user_group")
    public void consumeEvent(String message) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(message.split(":")[0]);
        auditLog.setDetails(message);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }
}

