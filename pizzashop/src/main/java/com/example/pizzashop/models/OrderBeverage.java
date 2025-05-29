package com.example.pizzashop.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orderbeverage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBeverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "beverage_id")
    private Beverage beverage;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private Float price;
}