package com.PictureThis.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.PictureThis.PictureThis.chat.model.ChatMessage;
import com.PictureThis.PictureThis.chat.service.ChatService;
import com.PictureThis.PictureThis.chat.dto.GameUpdateDto;
import com.PictureThis.PictureThis.user.dto.UserDto;

public class ChatServiceTest {
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);

        // Skapar en spy, så vi kan verifera privata anrop t.ex. startRound()
        chatService = Mockito.spy(new ChatService(messagingTemplate));
    }

    // Vid rätt gissning:
    // - ändrar state till ROUND_END
    // - broadcastGameUpdate("CORRECT_GUESS", ...) ska skickas
    // - startRound() ska anropas
    @Test
    void handleGuess_CorrectGuess_ShouldChangeStateAndBroadcast() {
        // Arrange
        chatService.getGameSession().setState(ChatService.SessionState.DRAWING);
        chatService.getGameSession().setCurrentWord("apple");

        // Lägger till två spelare & sätter "kalle" som ritare
        var drawer = new UserDto("1", "kalle");
        var guesser = new UserDto("2", "stina");

        chatService.getGameSession().getPlayers().add(drawer);
        chatService.getGameSession().getPlayers().add(guesser);
        chatService.getGameSession().setCurrentDrawer(drawer);

        // Gissaren skriver rätt ord
        ChatMessage guess = new ChatMessage("stina", "apple");

        // Mockar bort startRound, för att inte ändra tillbaka state till Drawing
        doNothing().when(chatService).startRound();

        // Act
        chatService.handleGuess(guess);

        // Assert
        assert chatService.getGameSession().getState() == ChatService.SessionState.ROUND_END;

        // Kollar att broadcastGameUpdate körs
        verify(messagingTemplate).convertAndSend(eq("/topic/game-updates"), any(GameUpdateDto.class));
        // Kollar att startRound() anropas
        verify(chatService).startRound();

    }

    // Vid fel gissning: ska inte hända något, alltså:
    // - state ska fortfarande vara DRAWING
    // - inget meddelande ska skickas
    // - startRound() ska inte anropas
    @Test
    void handleGuess_WrongGuess_ShouldDoNothing() {
        // Arrange
        chatService.getGameSession().setState(ChatService.SessionState.DRAWING);
        chatService.getGameSession().setCurrentWord("banana");

        // Lägger till två spelare & sätter "kalle" som ritare
        var drawer = new UserDto("1", "kalle");
        var guesser = new UserDto("2", "stina");

        chatService.getGameSession().getPlayers().add(drawer);
        chatService.getGameSession().getPlayers().add(guesser);
        chatService.getGameSession().setCurrentDrawer(drawer);

        // Passerar fel ord som gissning
        ChatMessage guess = new ChatMessage("stina", "apple");

        // Mockar bort startRound
        doNothing().when(chatService).startRound();

        // Act
        chatService.handleGuess(guess);

        // Assert
        assert chatService.getGameSession().getState() == ChatService.SessionState.DRAWING;

        verify(messagingTemplate, never()).convertAndSend(eq("/topic/game-updates"), any(GameUpdateDto.class));
        verify(chatService, never()).startRound();

    }
}
