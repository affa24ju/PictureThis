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
import com.PictureThis.PictureThis.user.dto.UserDto;

public class ChatServiceTest {
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);

        // Skapar en spy, så vi kan verifera privata anrop
        chatService = Mockito.spy(new ChatService(messagingTemplate));
    }

    // Vid rätt state & rätt gissning: ändrar state till ROUND_END
    // anropar broadcastGameState och startRound
    @Test
    void handleGuess_CorrectGuess_ShouldChangeStateAndBroadcast() {
        // Arrange
        chatService.getGameSession().setState(ChatService.SessionState.DRAWING);
        chatService.getGameSession().setCurrentWord("apple");

        // Lägger till en spelare, annars får null point exception
        chatService.getGameSession().getPlayers().add(new UserDto("1", "kalle"));

        ChatMessage guess = new ChatMessage("kalle", "apple");

        // Mockar bort startRound, annars sätter den tillbaka state till Drawing
        doNothing().when(chatService).startRound();
        // Act
        chatService.handleGuess(guess);

        // Assert
        assert chatService.getGameSession().getState() == ChatService.SessionState.ROUND_END;

        verify(chatService).broadcastGameState(contains("Correct guess by kalle!"));
        verify(chatService).startRound();

    }

    // Vid rätt state & fel gissning: ska inte hända något
    @Test
    void handleGuess_WrongState_ShouldDoNothing() {
        // Arrange
        chatService.getGameSession().setState(ChatService.SessionState.DRAWING);
        chatService.getGameSession().setCurrentWord("banana");

        // Passerar fel ord
        ChatMessage guess = new ChatMessage("kalle", "apple");

        // Act
        chatService.handleGuess(guess);

        // Assert
        assert chatService.getGameSession().getState() == ChatService.SessionState.DRAWING;

        verify(chatService, never()).broadcastGameState(anyString());
        verify(chatService, never()).startRound();

    }
}
