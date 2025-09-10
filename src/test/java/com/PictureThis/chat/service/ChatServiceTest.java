package com.PictureThis.chat.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.PictureThis.PictureThis.chat.model.ChatMessage;
import com.PictureThis.PictureThis.chat.service.ChatService;

public class ChatServiceTest {
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);

        // Skapar en spy, så vi kan verifera privata anrop
        chatService = Mockito.spy(new ChatService(messagingTemplate));
    }

    @Test
    void handleGuess_CorrectGuess_ShouldChangeStateAndBroadcast() {
        // Arrange
        chatService.getGameSession().setState(ChatService.SessionState.DRAWING);
        chatService.getGameSession().setCurrentWord("apple");

        ChatMessage guess = new ChatMessage("kalle", "apple");

        // Act
        chatService.handleGuess(guess);

        // Assert
        assert chatService.getGameSession().getState() == ChatService.SessionState.DRAWING;

        verify(chatService).broadcastGameState(contains("Correct guess by kalle!"));
        verify(chatService).startRound();

    }

    @Test
    void handleGuess_WrongState_DoesNothing() {
        // Arrange

    }
}
