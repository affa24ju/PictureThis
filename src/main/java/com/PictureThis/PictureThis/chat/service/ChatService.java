package com.PictureThis.PictureThis.chat.service;

import java.util.Random;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.PictureThis.PictureThis.chat.model.ChatMessage;
import com.PictureThis.PictureThis.chat.model.ChatSession;
import com.PictureThis.PictureThis.user.dto.UserDto;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSession gameSession;
    private final Random random = new Random();

    public enum SessionState {
        WAITING_FOR_PLAYERS,
        DRAWING,
        ROUND_END
    }

    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.gameSession = new ChatSession();
    }

    public ChatSession getGameSession() {
        return gameSession;
    }

    public void playerJoined(UserDto player) {
        if (gameSession.getPlayers().stream().noneMatch(p -> p.userName().equals(player.userName()))) {
            gameSession.getPlayers().add(player);
        }
        broadcastGameState("Player " + player.userName() + " has joined.");

        if (gameSession.getPlayers().size() == 2 && gameSession.getState() == SessionState.WAITING_FOR_PLAYERS) {
            startRound();
        }
    }

    public void playerLeft(UserDto player) {
        gameSession.getPlayers().removeIf(p -> p.userName().equals(player.userName()));
        broadcastGameState("Player " + player.userName() + " has left.");
        // Om spelaren som lämnade var den som ritade, välj en ny ritare och starta en
        // ny runda
        if (gameSession.getCurrentDrawer() != null
                && gameSession.getCurrentDrawer().userName().equals(player.userName())) {
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

    public void startRound() {
        gameSession.setState(SessionState.DRAWING);
        UserDto drawer = getNextDrawer();
        String word = gameSession.getWordList().get(random.nextInt(gameSession.getWordList().size()));
        gameSession.setCurrentWord(word);

        messagingTemplate.convertAndSendToUser(drawer.userName(), "/queue/game-state", word); // TODO skicka till alla
                                                                                              // istället, för
                                                                                              // convertAndSendToUser
                                                                                              // funkar inte tror jag
        broadcastGameState("New round started! " + drawer.userName() + " is drawing.");
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

        if (message.getMessageContent().equalsIgnoreCase(gameSession.getCurrentWord())) {
            gameSession.setState(SessionState.ROUND_END);
            broadcastGameState(
                    "Correct guess by " + message.getUserName() + "! The word was: " + gameSession.getCurrentWord());
            startRound();
        }
    }

    public void broadcastGameState(String notification) {
        ChatMessage message = new ChatMessage("System", notification);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
