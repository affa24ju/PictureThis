package com.PictureThis.PictureThis.drawing.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.PictureThis.PictureThis.drawing.model.Line;

@Controller
public class LineController {

    @MessageMapping("/draw")
    @SendTo("/topic/line")
    public Line sendLine(Line line) {
        return line;
    }

}
