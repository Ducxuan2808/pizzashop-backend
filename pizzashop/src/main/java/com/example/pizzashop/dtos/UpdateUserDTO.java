package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDTO {

    private String username;

    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("full_name")
    private String fullName;

    private String email;
    private String phone;
    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("role_id")
    private Long roleId;
}
