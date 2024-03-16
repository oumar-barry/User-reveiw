package com.barry.full.controller;

import com.barry.full.entity.User;
import com.barry.full.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private ReviewService reviewService;

    @GetMapping("/list")
    public Object getReviews(Principal principal){
        return this.reviewService.getReview();
    }


}
