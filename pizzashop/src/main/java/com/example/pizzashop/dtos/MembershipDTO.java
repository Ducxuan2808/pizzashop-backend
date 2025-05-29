package com.example.pizzashop.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipDTO {

    private String phone;

    @JsonProperty("total_spent")
    private Float totalSpent;

    @JsonProperty("membership_tier")
    private String membershipTier;

    @JsonProperty("discount_rate")
    private Float discountRate;
}