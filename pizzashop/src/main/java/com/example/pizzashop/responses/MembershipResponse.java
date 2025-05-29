package com.example.pizzashop.responses;

import com.example.pizzashop.models.Membership;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private List<String> errors;

    @JsonProperty("membership")
    private Membership membership;

    @JsonProperty("is_active")
    private Boolean active;//thuộc về admin
}
