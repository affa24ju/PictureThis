package com.PictureThis.PictureThis.chat.dto;

import java.util.List;
import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.chat.service.ChatService.SessionState;

public record GameSessionDto(
    List<UserDto> players,
    String currentWord,
    UserDto currentDrawer,
    SessionState state
) {}
