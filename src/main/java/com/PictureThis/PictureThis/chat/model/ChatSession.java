package com.PictureThis.PictureThis.chat.model;

import java.util.List;

import com.PictureThis.PictureThis.user.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    private List<String> wordLists;
    private List<UserDto> userDtos;
    private String currentWord;
    private String currentDrawer;
}
