package com.example.pizzashop.services;

import com.example.pizzashop.dtos.ReviewDTO;
import com.example.pizzashop.models.Review;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IReviewService {
    Review createReview(ReviewDTO reviewDTO) throws Exception;
    Review updateReview(Long id, ReviewDTO reviewDTO) throws Exception;
    void deleteReview(Long id) throws Exception;
    List<Review> getByPizzaId(Long id) throws Exception;
    List<Review> getByOrderId(Long id) throws Exception;
    Review getByOrderDetailId(Long orderDetailId);
    boolean existByOrderDetailId(Long orderDetailId);
    Integer getCountReviewByPizzaId(long pizzaId);

}
