package com.PictureThis.PictureThis.user.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PictureThis.PictureThis.chat.service.ChatService;
import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.user.dto.UserLoginDto;
import com.PictureThis.PictureThis.user.models.User;
import com.PictureThis.PictureThis.user.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;

    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody User user) {
        return userService.addNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> loginUser(@RequestBody UserLoginDto userLoginDto) {
        String userName = userLoginDto.userName();
        String password = userLoginDto.password();
        UserLoginDto validatedUser = userService.login(userName, password);
        if (validatedUser == null) {
            // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        chatService.playerJoined(new UserDto(validatedUser.id(), validatedUser.userName()));
        return ResponseEntity.ok(validatedUser);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

}
