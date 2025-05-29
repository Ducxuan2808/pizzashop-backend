package com.example.pizzashop.controllers;

import com.example.pizzashop.dtos.OrderDTO;
import com.example.pizzashop.dtos.OrderDetailDTO;
import com.example.pizzashop.dtos.ReviewDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Review;
import com.example.pizzashop.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Review review = reviewService.createReview(reviewDTO);
            return ResponseEntity.ok(review);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateReview(@Valid @PathVariable("id") Long id,
                                               @RequestBody ReviewDTO reviewDTO){
        try {
            Review review = reviewService.updateReview(id,reviewDTO);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteReview(@PathVariable Long id){
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pizza/{id}")
    public ResponseEntity<?> getReviewsByPizzaId(@PathVariable("id") Long id){
        try {
            List<Review> reviews = reviewService.getByPizzaId(id);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<?> getReviewsByOrderId(@PathVariable("id") Long id){
        try {
            List<Review> reviews = reviewService.getByOrderId(id);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewByOrderDetailId(@PathVariable("id") Long id){
        try {
            Review review = reviewService.getByOrderDetailId(id);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
