package com.electricity.backend.service;

import com.electricity.backend.dto.Notification;
import com.electricity.backend.entity.User;
import com.electricity.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    // Map of User ID -> SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Infinite timeout
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        // Send an initial dummy event to establish connection
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendToUser(String userId, String message, String type) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                Notification notif = new Notification(UUID.randomUUID().toString(), message, type);
                emitter.send(SseEmitter.event().name("notification").data(notif));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    public void sendToRole(String role, String message, String type) {
        List<User> users = userRepository.findByRole(role.toUpperCase());
        for (User user : users) {
            sendToUser(user.getUserId(), message, type);
        }
    }
}
