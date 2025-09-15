package com.PictureThis.PictureThis.user.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.PictureThis.PictureThis.user.dto.UserLoginDto;
import com.PictureThis.PictureThis.user.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        // Förväntar badRequest om userName är tomt, @NotBlank fungerar då
        @Test
        public void testRegisterUser_InvalidUserName() throws Exception {
                var user = new User();
                user.setUserName("");
                user.setPassword("kalle123");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        // Test login med rätt credential
        @Test
        public void testLoginUser_ValidCredentials() throws Exception {
                // Arrange, registrera en user först
                var user = new User();
                user.setUserName("nisse");
                user.setPassword("nisse123");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/users/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(MockMvcResultMatchers.status().isOk());

                // Act: Logga in med samma credential
                var loginDto = new UserLoginDto(null, "nisse", "nisse123");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/users/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginDto)))
                                // Assert
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                // Kollar att user-objektet returneras korrekt
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.userName").value("nisse"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.password").doesNotExist())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").exists())
                                // Kollar om token finns
                                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());

        }

        // Test: login med invalid user credential
        @Test
        public void testLoginUser_InvalidCredentials() throws Exception {
                // Arrange: registrera en user först

                // Act: Logga in med fel userName/ password

                // Assert:

                // Hmmm...kanske får nullPointer exception, samma som igår kväll!!
                // För att vid fel credential returnerar userService.login null
                // Kanske bättre att kolla, validateuser == null & returnera 401 Unauthorized

        }

}
