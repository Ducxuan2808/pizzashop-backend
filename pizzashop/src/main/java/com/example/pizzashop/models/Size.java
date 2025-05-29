package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "size")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "size_name", nullable = false)
    @JsonProperty("size_name")
    private String sizeName;

    @Column(name = "price_multiplier", nullable = false)
    @JsonProperty("price_multiplier")
    private Float priceMultiplier;

    @Column(name = "is_active")
    @JsonProperty("is_active")
    private Boolean active;
}