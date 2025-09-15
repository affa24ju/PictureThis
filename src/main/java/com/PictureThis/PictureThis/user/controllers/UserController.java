package com.PictureThis.PictureThis.user.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PictureThis.PictureThis.JWTsecurity.JWTUtil;
import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.user.dto.UserLoginDto;
import com.PictureThis.PictureThis.user.models.User;
import com.PictureThis.PictureThis.user.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JWTUtil JWTUtil;

    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody User user) {
        return userService.addNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserLoginDto userLoginDto) {
        String userName = userLoginDto.userName();
        String password = userLoginDto.password();
        UserLoginDto validatedUser = userService.login(userName, password);

        if (validatedUser != null) {
            String token = JWTUtil.generateToken(userName);
            return ResponseEntity.ok(Map.of(
                    "user", validatedUser,
                    "token", token));

        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

}
