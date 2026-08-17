package org.Activity;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandler extends TextWebSocketHandler {

    // Store active WebSocket sessions
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    // Store like counts for each post
    private final ConcurrentHashMap<String, Integer> postLikes = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New WebSocket connection established");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject jsonMessage = new JSONObject(message.getPayload());
            String type = jsonMessage.getString("type");
            
            if ("like".equals(type)) {
                handleLikeEvent(jsonMessage);
            }
        } catch (JSONException e) {
            System.err.println("Error parsing WebSocket message: " + e.getMessage());
        }
    }

    private void handleLikeEvent(JSONObject message) throws Exception {
        String postId = message.getString("postId");
        String likedBy = message.getString("likedBy");
        
        // Update like count
        int currentLikes = postLikes.getOrDefault(postId, 0);
        postLikes.put(postId, currentLikes + 1);
        
        // Create response message
        JSONObject response = new JSONObject();
        response.put("type", "like");
        response.put("postId", postId);
        response.put("likedBy", likedBy);
        response.put("totalLikes", postLikes.get(postId));
        
        // Broadcast to all connected clients
        TextMessage broadcastMessage = new TextMessage(response.toString());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(broadcastMessage);
            }
        }
    }
} 