package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pizzaimage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PizzaImage {
    public static final int MAXIMUM_IMAGES_PER_PRODUCT=5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    @JsonIgnore
    private Pizza pizza;

    @Column(name = "imageUrl",length = 300)
    @JsonProperty("image_url")
    private String image_url;
}