package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pizza")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(PizzaListener.class)
public class Pizza extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "base_price")
    private Float basePrice;

    @Column(name = "thumbnail",length = 300)
    private String thumbnail;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty("pizza_images")
    private List<PizzaImage> pizzaImages;

    @Column(name = "is_active")
    private Boolean active;//thuộc về admin

}
