package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "review")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonProperty("user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    @JsonProperty("pizza_id")
    private Pizza pizza;

    @ManyToOne
    @JoinColumn(name = "orderdetail_id")
    @JsonProperty("orderdetail_id")
    private OrderDetail orderDetail;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "review_time")
    @JsonProperty("review_time")
    private Date reviewTime;

    @Column(name = "is_active")
    private Boolean active;//thuộc về admin
}

