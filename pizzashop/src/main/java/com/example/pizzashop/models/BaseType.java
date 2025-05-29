package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "basetype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_name", nullable = false, length = 50)
    @JsonProperty("base_name")
    private String baseName;

    @Column(name = "price")
    @JsonProperty("price")
    private Float price;

    @Column(name = "is_active")
    @JsonProperty("is_active")
    private Boolean active;
}