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

    public void playerJoined(UserDto player) {
        gameSession.addPlayer(player);
        broadcastGameState("Player " + player.userName() + " has joined.");

        if (gameSession.getPlayers().size() == 2 && gameSession.getState() == SessionState.WAITING_FOR_PLAYERS) {
            startRound();
        }
    }

    // TODO: PlayerLeft metod. om spelaren som lämnar är den som ritar, välj ny ritare och starta om rundan

    private void startRound() {
        gameSession.setState(SessionState.DRAWING);
        UserDto drawer = gameSession.getNextDrawer();
        String word = gameSession.getWordList().get(random.nextInt(gameSession.getWordList().size()));
        gameSession.setCurrentWord(word);

        messagingTemplate.convertAndSendToUser(drawer.userName(), "/queue/game-state", word); // TODO skicka till alla istället, för convertAndSendToUser funkar inte tror jag
        broadcastGameState("New round started! " + drawer.userName() + " is drawing.");
    }

    public void handleGuess(ChatMessage message) {
        if (gameSession.getState() != SessionState.DRAWING) {
            return;
        }

        if (message.getMessageContent().equalsIgnoreCase(gameSession.getCurrentWord())) {
            gameSession.setState(SessionState.ROUND_END);
            broadcastGameState("Correct guess by " + message.getUserName() + "! The word was: " + gameSession.getCurrentWord());
            startRound();
        } 
    }

    private void broadcastGameState(String notification) {
        ChatMessage message = new ChatMessage("System", notification);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
