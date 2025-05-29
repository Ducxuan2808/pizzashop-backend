package com.example.pizzashop.dtos;

import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    @JsonProperty("user_id")
    private Long user_id;

    @JsonProperty("pizza_id")
    private Long pizza_id;

    @JsonProperty("orderdetail_id")
    private Long orderdetail_id;

    @JsonProperty("rating")
    private int rating;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("review_time")
    private Date reviewTime;
}
