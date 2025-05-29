package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "membership")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "total_spent")
    @JsonProperty("total_spent")
    private Float totalSpent;

    @Column(name = "membership_tier")
    @JsonProperty("membership_tier")
    private String membershipTier;

    @Column(name = "discount_rate")
    @JsonProperty("discount_rate")
    private Float discountRate;

    @Column(name = "is_active")
    private Boolean active;//thuộc về admin

    public static final String MEMBER_BRONZE = "Bronze";
    public static final String MEMBER_SILVER = "Silver";
    public static final String MEMBER_GOLD = "Gold";
    public static final String MEMBER_PLATINUM = "Platinum";

    public static final Float DISCOUNT_BRONZE  = 0.0f;
    public static final Float DISCOUNT_SILVER = 2f;
    public static final Float DISCOUNT_GOLD = 3f;
    public static final Float DISCOUNT_PLATINUM = 5f;


}

