package com.barry.full.controller;

import com.barry.full.entity.Jwt;
import com.barry.full.entity.User;
import com.barry.full.security.JwtService;
import com.barry.full.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(value = "/auth")
public class UserController {
    private UserService userService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return this.userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDto userDto){
        //return userDto;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.username(),userDto.password())
        );


        if(authentication.isAuthenticated()){
            User usr = (User) authentication.getPrincipal();
            //Expire all user's tokens
            this.jwtService.expireUserTokens(usr);
            Map<String, String> bearer = this.jwtService.generateToken(userDto.username());
            Jwt jwt = Jwt
                    .builder()
                    .expire(false)
                    .token(bearer.get("bearer"))
                    .user(usr)
                    .build();

            this.jwtService.saveJwt(jwt);
            return bearer;

        }

        return null;

    }

    @PostMapping("/logout")
    public String logout(){
        return this.jwtService.logout();
    }
}
