package com.example.pizzashop.responses;

import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;

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

    @JsonProperty("is_active")
    private Boolean active;//thuộc về admin

    public static  ReviewResponse fromReview(Review review){
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(review.getId())
                .user_id(review.getUser().getId())
                .pizza_id(review.getPizza().getId())
                .orderdetail_id((review.getOrderDetail().getId()))
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewTime((review.getReviewTime()))
                .active(review.getActive())
                .build();

        return reviewResponse;
    }
}
