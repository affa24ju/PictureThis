package com.PictureThis.PictureThis.user.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.user.models.User;

@Service
public class UserService {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User addNewUser(User user) {
        String encrtptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encrtptedPassword);
        return mongoOperations.insert(user);

    }

    public List<UserDto> findAllUsers() {
        return mongoOperations.findAll(UserDto.class);
    }

}
