package com.PictureThis.PictureThis.chat.dto;

public class GameUpdateDto {
    private String eventType;
    private String eventContent;

    public GameUpdateDto() {}

    public GameUpdateDto(String eventType, String eventContent) {
        this.eventType = eventType;
        this.eventContent = eventContent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }
}
