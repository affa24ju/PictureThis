package com.PictureThis.PictureThis.chat.service;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.PictureThis.PictureThis.chat.model.ChatMessage;
import com.PictureThis.PictureThis.chat.model.ChatSession;
import com.PictureThis.PictureThis.user.dto.UserDto;
// import com.PictureThis.PictureThis.chat.dto.GameSessionDto;
import com.PictureThis.PictureThis.chat.dto.GameUpdateDto;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSession gameSession;

    public enum SessionState {
        WAITING_FOR_PLAYERS,
        CHOOSING_WORD,
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
        Map<String, Object> content = new HashMap<>();
        content.put("userName", player.userName());
        broadcastGameUpdate("PLAYER_JOINED", content);
        System.out.println(
                "användare i chatsessionen: " + gameSession.getPlayers().stream().map(UserDto::userName).toList()
                        + ", state: " + gameSession.getState());

        if (gameSession.getPlayers().size() == 2 && gameSession.getState() == SessionState.WAITING_FOR_PLAYERS) {
            startRound();
        }
    }

    public void playerLeft(UserDto player) {
        gameSession.getPlayers().removeIf(p -> p.userName().equals(player.userName()));

        Map<String, Object> content = new HashMap<>();
        content.put("userName", player.userName());
        broadcastGameUpdate("PLAYER_LEFT", content);
        System.out.println(
                "användare i chatsessionen: " + gameSession.getPlayers().stream().map(UserDto::userName).toList()
                        + ", state: " + gameSession.getState());

        // Om färre än 2 spelare, sätt state till WAITING_FOR_PLAYERS
        if (gameSession.getPlayers().size() < 2) {
            gameSession.setState(SessionState.WAITING_FOR_PLAYERS);
            gameSession.setCurrentDrawer(null);
            gameSession.setCurrentDrawerIndex(-1);
            gameSession.setCurrentWord(null);
            return;
        }

        // Om spelaren som lämnade var den som ritade, välj en ny ritare och starta en
        // ny runda
        if (gameSession.getCurrentDrawer() != null
                && gameSession.getCurrentDrawer().userName().equals(player.userName())) {
            gameSession.setCurrentDrawer(null);
            startRound();
        } else if (gameSession.getCurrentDrawerIndex() >= gameSession.getPlayers().size()) {
            gameSession.setCurrentDrawerIndex(gameSession.getPlayers().size() - 1);
        }
    }

    public void startRound() {
        gameSession.setState(SessionState.CHOOSING_WORD);
        UserDto drawer = getNextDrawer();

        Collections.shuffle(gameSession.getWordList());
        List<String> wordSelection = new ArrayList<>(gameSession.getWordList().subList(0, 3));
        System.out.println("Ny runda startad. Ritare: " + drawer.userName() + ", Ord att välja mellan: " + wordSelection);
        Map<String, Object> content = new HashMap<>();
        content.put("userName", drawer.userName());
        broadcastGameUpdate("NEW_ROUND", content);
        messagingTemplate.convertAndSendToUser(drawer.userName(), "/queue/game-state", wordSelection);

        /* 
         String word = gameSession.getWordList().get(random.nextInt(gameSession.getWordList().size()));
         gameSession.setCurrentWord(word);
         System.out.println("Ny runda startad. Ritare: " + drawer.userName() + ", Ord: " + word);
         
         Map<String, Object> content = new HashMap<>();
         content.put("userName", drawer.userName());
         broadcastGameUpdate("NEW_ROUND", content);
         messagingTemplate.convertAndSendToUser(drawer.userName(), "/queue/game-state", word);
         */

    }

    public void handleWordSelection(String userName, String selectedWord) {
        if (gameSession.getCurrentDrawer() == null || !gameSession.getCurrentDrawer().userName().equals(userName)) {
            return;
        }
        if (selectedWord == null || selectedWord.isEmpty()) {
            selectedWord = gameSession.getWordList().get(0);
        }

        gameSession.setCurrentWord(selectedWord);
        gameSession.setState(SessionState.DRAWING);

        Map<String, Object> content = new HashMap<>();
        content.put("userName", userName);
        broadcastGameUpdate("WORD_SELECTED", content);
        //messagingTemplate.convertAndSendToUser(userName, "/queue/game-state", selectedWord);

        System.out.println("Ritare " + userName + " valde ordet: " + selectedWord);
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

        if (!message.getUserName().equals(gameSession.getCurrentDrawer().userName())
                && message.getMessageContent() != null
                && message.getMessageContent().equalsIgnoreCase(gameSession.getCurrentWord())) {
            gameSession.setState(SessionState.ROUND_END);

            Map<String, Object> content = new HashMap<>();
            content.put("userName", message.getUserName());
            content.put("word", gameSession.getCurrentWord());
            broadcastGameUpdate("CORRECT_GUESS", content);

            // Fördröjning innan ny runda startar
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            startRound();
        }
    }

    

    private void broadcastGameUpdate(String event, Map<String, Object> content) {
        GameUpdateDto update = new GameUpdateDto(event, content);
        messagingTemplate.convertAndSend("/topic/game-updates", update);
        // messagingTemplate.convertAndSend("/topic/game-session", toGameSessionDto());
    }



    // TODO när ny spelare går med, skicka hela gameSession
    // private GameSessionDto toGameSessionDto() {
    // return new GameSessionDto(
    // gameSession.getPlayers(),
    // gameSession.getCurrentWord(),
    // gameSession.getCurrentDrawer(),
    // gameSession.getState());
    // }
}
