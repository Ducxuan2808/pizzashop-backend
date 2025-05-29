package com.example.pizzashop.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "beverage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Beverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price")
    private Float price;
}
