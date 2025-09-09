package com.PictureThis.PictureThis.chat.service;

import java.util.Random;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.PictureThis.PictureThis.chat.model.ChatMessage;
import com.PictureThis.PictureThis.chat.model.ChatSession;
import com.PictureThis.PictureThis.user.dto.UserDto;
import com.PictureThis.PictureThis.chat.dto.GameSessionDto;
import com.PictureThis.PictureThis.chat.dto.GameUpdateDto;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSession gameSession;
    private final Random random = new Random();

    public enum SessionState {
        WAITING_FOR_PLAYERS,
        // CHOOSING_WORD,
        DRAWING,
        ROUND_END
    }

    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.gameSession = new ChatSession();
    }

    public void playerJoined(UserDto player) {
        if (gameSession.getPlayers().stream().noneMatch(p -> p.userName().equals(player.userName()))) {
            gameSession.getPlayers().add(player);
        }
        broadcastGameUpdate("PLAYER_JOINED", player.userName());
        System.out.println("användare i chatsessionen: " + gameSession.getPlayers().stream().map(UserDto::userName).toList());

        if (gameSession.getPlayers().size() == 2 && gameSession.getState() == SessionState.WAITING_FOR_PLAYERS) {
            startRound();
        }
    }

    public void playerLeft(UserDto player) {
        gameSession.getPlayers().removeIf(p -> p.userName().equals(player.userName()));
        broadcastGameUpdate("PLAYER_LEFT", player.userName());
        System.out.println(
                "användare i chatsessionen: " + gameSession.getPlayers().stream().map(UserDto::userName).toList());
        // Om spelaren som lämnade var den som ritade, välj en ny ritare och starta en ny runda
        if (gameSession.getCurrentDrawer() != null && gameSession.getCurrentDrawer().userName().equals(player.userName())) {
            gameSession.setCurrentDrawer(null);
            gameSession.setCurrentDrawerIndex(-1);
            if (!gameSession.getPlayers().isEmpty()) {
                startRound();
            } else {
                gameSession.setState(SessionState.WAITING_FOR_PLAYERS);
            }
        } else if (gameSession.getCurrentDrawerIndex() >= gameSession.getPlayers().size()) {
            gameSession.setCurrentDrawerIndex(gameSession.getPlayers().size() - 1);
        }
    }

    private void startRound() {
        gameSession.setState(SessionState.DRAWING);
        UserDto drawer = getNextDrawer();
        String word = gameSession.getWordList().get(random.nextInt(gameSession.getWordList().size()));
        gameSession.setCurrentWord(word);
        System.out.println("Ny runda startad. Ritare: " + drawer.userName() + ", Ord: " + word);

        messagingTemplate.convertAndSendToUser(drawer.userName(), "/queue/game-state", word); // TODO skicka till alla istället, för convertAndSendToUser funkar inte tror jag
        broadcastGameUpdate("NEW_ROUND", drawer.userName());
    }

    private UserDto getNextDrawer() {
        if (gameSession.getPlayers().isEmpty()) {
            return null;
        }
        gameSession.setCurrentDrawerIndex((gameSession.getCurrentDrawerIndex() + 1) % gameSession.getPlayers().size());
        UserDto drawer = gameSession.getPlayers().get(gameSession.getCurrentDrawerIndex());
        gameSession.setCurrentDrawer(drawer);
        return drawer;
    }

    public void handleGuess(ChatMessage message) {
        if (gameSession.getState() != SessionState.DRAWING) {
            return;
        }

        if (message.getMessageContent() != null
                && message.getMessageContent().equalsIgnoreCase(gameSession.getCurrentWord())) {
            gameSession.setState(SessionState.ROUND_END);
            broadcastGameUpdate("CORRECT_GUESS", message.getUserName());
            startRound();
        }
    }

    private void broadcastGameUpdate(String eventType, String eventContent) {
        GameUpdateDto update = new GameUpdateDto(eventType, eventContent);
        messagingTemplate.convertAndSend("/topic/game-updates", update);
        messagingTemplate.convertAndSend("/topic/game-session", toGameSessionDto());
    }

    private GameSessionDto toGameSessionDto() {
        return new GameSessionDto(
                gameSession.getPlayers(),
                gameSession.getCurrentWord(),
                gameSession.getCurrentDrawer(),
                gameSession.getState());
    }
}
