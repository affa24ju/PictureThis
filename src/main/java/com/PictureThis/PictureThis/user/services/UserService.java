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

    // Skapar ny user och ger password en encoderad version
    public User addNewUser(User user) {
        // ifall namnet redan finns kastas ett felmeddelande
        if (userNameExists(user.getUserName())) {
            throw new IllegalArgumentException("Username already exists");
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return mongoOperations.insert(user);

    }

    // Hämtar user baserat på userName och kollar om password stämmer
    // gör den inte det kastas ett felmeddelande
    public UserLoginDto login(String userName, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        User user = mongoOperations.findOne(query, User.class);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return new UserLoginDto(user.getId(), user.getUserName(), null);
        }
        throw new IllegalArgumentException("Invalid username or password");

    }

    // hämtar alla users och mappar dem till UserDto för att inte skicka med
    // password
    public List<UserDto> findAllUsers() {
        List<User> users = mongoOperations.findAll(User.class);
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUserName()))
                .toList();

    }

    // kollar om userName redan finns i databasen
    public boolean userNameExists(String userName) {
        Query nameQuery = new Query();
        nameQuery.addCriteria(Criteria.where("userName").is(userName));
        return mongoOperations.exists(nameQuery, User.class);
    }

}
