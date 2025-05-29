package com.example.pizzashop.services;

import com.example.pizzashop.dtos.ReviewDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.Review;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.OrderDetailRepository;
import com.example.pizzashop.repositories.PizzaRepository;
import com.example.pizzashop.repositories.ReviewRepository;
import com.example.pizzashop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService{
    private final PizzaRepository pizzaRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    @Override
    public Review createReview(ReviewDTO reviewDTO) throws Exception {
        User user = userRepository.findById(reviewDTO.getUser_id())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy người dùng có id: "+ reviewDTO.getUser_id()));
        OrderDetail orderDetail = orderDetailRepository.findById(reviewDTO.getOrderdetail_id())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy chi tiết đơn hàng có id: "+ reviewDTO.getOrderdetail_id()));
        Review review = Review.builder()
                .user(user)
                .pizza(orderDetail.getPizza())
                .orderDetail(orderDetail)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .reviewTime(reviewDTO.getReviewTime())
                .build();
        reviewRepository.save(review);
        return review;
    }

    @Override
    public Review updateReview(Long id, ReviewDTO reviewDTO) throws Exception {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy review có id: "+ id));
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setReviewTime(reviewDTO.getReviewTime());
        reviewRepository.save(review);
        return review;
    }


    @Override
    public void deleteReview(Long id) throws Exception {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy review có id: "+ id));
        review.setActive(false);
        reviewRepository.save(review);
    }

    @Override
    public List<Review> getByPizzaId(Long id) throws Exception {
        List<Review> listReview = reviewRepository.getByPizzaId(id);
        return listReview;
    }

    @Override
    public List<Review> getByOrderId(Long id) throws Exception {
        List<Review> listReview = reviewRepository.getByOrderId(id);
        return listReview;
    }

    @Override
    public Review getByOrderDetailId(Long id){
        Review review = reviewRepository.getByOrderDetailId(id)
                .orElse(null);
        return review;
    }

    @Override
    public boolean existByOrderDetailId(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow(null);
        return reviewRepository.existsByOrderDetail(orderDetail);
    }
}
