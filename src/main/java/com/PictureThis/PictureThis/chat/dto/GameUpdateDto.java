package com.PictureThis.PictureThis.chat.dto;

import java.util.Map;

public record GameUpdateDto(String event, Map<String, Object> content) {}
