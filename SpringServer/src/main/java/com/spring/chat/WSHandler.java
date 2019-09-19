package com.spring.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WSHandler extends TextWebSocketHandler {
	public static HashMap<WebSocketSession, Room> clientsRoom = new HashMap<WebSocketSession, Room>();
	public static HashMap<String, Room> rooms = new HashMap<String, Room>();

	
	public void handleTextMessage (WebSocketSession wss, TextMessage msg) throws IOException{
		System.out.println(msg);
		System.out.println(msg.getPayload());
		String msgStr = msg.getPayload();
		String[] parsedStr = msgStr.split("\\s");
		
		// If message isn't blank
		if(parsedStr.length > 1) { 
			
			// if it is a join request
			if (parsedStr[0].equals("join")) {
				String name = parsedStr[1];
				
				// if the room already exists
				if (rooms.containsKey(name)) {
		    		System.out.println("room exists: " + name);
		    		Room room = rooms.get(name);
		    		clientsRoom.put(wss, room);
		    		room.addSession(wss);
		    		
		    	// if the room doesn't exist
		    	} else {
		    		System.out.println("new room: " + name);
		    		Room room = new Room();
		    		System.out.println("room when created : " + room);
		    		room.addSession(wss);
		    		rooms.put(name, room);
		    		clientsRoom.put(wss, room);
		    	}	
				
			// if it is a regular message (not a join room request)
			} else {
				TextMessage msgToBroadcast = getTextMessage(msgStr);
				Room room = clientsRoom.get(wss);
				System.out.print(("Room before broadcast: " + room));
				room.broadcast(msgToBroadcast);
			}
			
		}
	}
	
    public TextMessage getTextMessage(String msg) {
    	String[] msgArr = msg.split("\\s");
    	
    	String username = msgArr[0];
    	String message = new String();
    	
    	for (int i = 1; i < msgArr.length; i++) {
    		message += msgArr[i] + " ";
    	}
 
    	String json = "{ \"user\" : \"" + username + "\", \"message\" : \"" + message + "\" }";
    	
    	TextMessage txtMsg = new TextMessage(json);
    	
    	return txtMsg;
    }

}
