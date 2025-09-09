package com.PictureThis.PictureThis.user.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.user.dto.UserLoginDto;
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

    public UserLoginDto login(String userName, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        User user = mongoOperations.findOne(query, User.class);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return new UserLoginDto(user.getId(), user.getUserName(), null);
        }
        return null;

    }

    public List<UserDto> findAllUsers() {
        List<User> users = mongoOperations.findAll(User.class);
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUserName()))
                .toList();

    }

}
