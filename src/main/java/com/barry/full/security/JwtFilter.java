package com.barry.full.security;

import com.barry.full.entity.Jwt;
import com.barry.full.entity.User;
import com.barry.full.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    public JwtFilter(JwtService jwtService, UserService userService, UserService userService1) {
        this.jwtService = jwtService;
        this.userService = userService1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        String token = null;
        Jwt jwtInDb = null;
        boolean isTokenExpired = true;

        String authorization = request.getHeader("Authorization");
        if(authorization != null  && authorization.startsWith("Bearer ")){
            token = authorization.substring(7);
            isTokenExpired = jwtService.isTokenExpired(token);
            username = jwtService.extractUsernameFromToken(token);
            jwtInDb = this.jwtService.findBytoken(token);

        }

        if(!isTokenExpired
                && !jwtInDb.isExpire()
                && jwtInDb.getUser().getUsername().equals(username)
                && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(upat);
        }

        filterChain.doFilter(request,response);

    }
}
