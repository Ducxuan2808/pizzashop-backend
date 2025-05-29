package com.example.pizzashop.responses;

import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.Role;
import com.example.pizzashop.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@Builder
public class UserResponse {
    private Long id;

    private String username;

    private String password;

    @JsonProperty("full_name")
    private String fullName;

    private String email;
    private String phone;
    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("role_id")
    private Role role;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("membership")
    private Membership membership;

    public  static  UserResponse fromUser(User user){
        return  UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .build();
    }
}
