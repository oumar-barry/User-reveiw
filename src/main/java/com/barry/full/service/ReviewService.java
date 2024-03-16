package com.barry.full.service;

import com.barry.full.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewService {
    public Object getReview(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Map.of(
                "username", user.getUsername(),
                "email", user.getEmail()
        );
    }
}
