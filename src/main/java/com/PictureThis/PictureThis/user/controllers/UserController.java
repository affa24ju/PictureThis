package com.PictureThis.PictureThis.user.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.user.dto.UserLoginDto;
import com.PictureThis.PictureThis.user.models.User;
import com.PictureThis.PictureThis.user.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // @PostMapping("/register")
    // // @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created status
    // public User registerUser(@RequestBody User user) {
    //     return userService.addNewUser(user);
    // }
    @PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    // We return proper HTTP status codes instead of a raw entity
    try {
        userService.addNewUser(user);
        // 201 Created with empty body (kan även retunera en  DTO vid behov)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (IllegalArgumentException e) {
        // Validation failure -> 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (org.springframework.dao.DuplicateKeyException e) {
        // användarnamn alraedy exists -> 409 Conflict
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
    }
}

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserLoginDto> loginUser(@RequestBody UserLoginDto userLoginDto) {
        String userName = userLoginDto.userName();
        String password = userLoginDto.password();
        return ResponseEntity.ok(
                userService.login(userName, password));
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

}
