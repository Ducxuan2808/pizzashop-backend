package com.example.pizzashop.kafka.events;

import com.example.pizzashop.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    public static final String REGISTERED = "USER_REGISTERED";
    public static final String UPDATED = "USER_UPDATED";
    public static final String DEACTIVATED = "USER_DEACTIVATED";
    public static final String PASSWORD_RESET = "USER_PASSWORD_RESET";

    private String eventType;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private long occurredAt;

    // Không đưa password vào event
    public static UserEvent of(String eventType, User user) {
        return UserEvent.builder()
                .eventType(eventType)
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().getRoleName() : null)
                .occurredAt(System.currentTimeMillis())
                .build();
    }
}
