package com.PictureThis.PictureThis.chat.dto;

import java.util.Map;

public class GameUpdateDto {
    private String event;
    private Map<String, Object> content;

    public GameUpdateDto() {}

    public GameUpdateDto(String event, Map<String, Object> content) {
        this.event = event;
        this.content = content;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
}
