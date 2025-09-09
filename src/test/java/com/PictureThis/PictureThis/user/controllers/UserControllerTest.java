package com.PictureThis.PictureThis.user.controllers;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.PictureThis.PictureThis.user.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.var;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterUser_ValidUser() throws Exception {
        var user = new User();
        user.setUserName("kalle");
        user.setPassword("kalle123");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Kollar om den returnerar samma userName
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("kalle"))
                // Det ska inte returnera samma password, för att det ska vara crypterad
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(Matchers.not("kalle123")))
                // Kollar om det finns id; id ska genereras automatiskt
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

    }

}
