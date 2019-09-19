package com.spring.chat;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Room {
	private ArrayList<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
	private ArrayList<TextMessage> messages = new ArrayList<TextMessage>();
	
    public void populateMessageHistory(WebSocketSession wss) throws IOException {
    	for (TextMessage msg : messages) {
    		wss.sendMessage(msg);
    	}
    }
	
	public synchronized void addSession(WebSocketSession wss) throws IOException {
		populateMessageHistory(wss);
		if (!sessions.contains(wss)) {
			System.out.println("Client added");
			sessions.add(wss);
			System.out.println("client list length: " + sessions.size());
		}
	}
	
	 public synchronized void broadcast(TextMessage msg) throws IOException {
		if (sessions != null) {
			System.out.println("# of sessions: " + sessions.size());
			messages.add(msg);
			for (WebSocketSession wss : sessions) {
					System.out.println("sending: " + msg.getPayload());
					if (wss.isOpen()) {
						wss.sendMessage(msg);
					}
			}
		}
	 }
	
}

