package com.PictureThis.PictureThis.chat.model;

import java.util.ArrayList;
import java.util.List;

import com.PictureThis.PictureThis.chat.service.ChatService.SessionState;
import com.PictureThis.PictureThis.user.dto.UserDto;

import lombok.Data;

@Data
public class ChatSession {

    private List<String> wordList;
    private List<UserDto> players = new ArrayList<>();
    private String currentWord;
    private UserDto currentDrawer;
    private int currentDrawerIndex = -1;
    private SessionState state = SessionState.WAITING_FOR_PLAYERS;

    public ChatSession() {
        this.wordList = List.of("apple", "banana", "car", "house", "tree", "dog", "cat", "sun", "moon", "star");
    }

}
