import java.io.IOException;

// Client will need access to the room and its websocket connection
public class ChatClient implements Listener {

    private WebSocketConnection _wsc;
    private Room _room = null;
    
    ChatClient(WebSocketConnection wsc) {
    	_wsc = wsc;
    }

    //reading messages from the client
    public void onMessage(String msg) throws IOException {
    	
    	String[] parsedMsg = msg.split("\\s");
    	
    	if (parsedMsg.length > 1) {
		    	if (parsedMsg[0].equals("join")) {
		    		System.out.println("room joined: " + parsedMsg[1]);
		    		_room = Room.getRoom(parsedMsg[1]);
		    		_room.addUser(this);
		    	} else {
		    		  // Send message back to client.
		    		System.out.println("msging room: " + _room);
		    		if (_room != null) {
		    			String json = getJSON(msg);
		    			_room.broadcast(json);
		    		} else {
		    			String errMsg = "User:  Please join a room before posting messages";
		    			String jsonStr = getJSON(errMsg);
		    			_wsc.sendMessage(jsonStr);
		    		}	   	
		    	}
    	}

    }
    
    public void onClose() throws IOException {
  
        System.out.println("connection closed");

    }
    
    public String getJSON(String msg) {
    	String[] msgArr = msg.split("\\s");
    	
    	String username = msgArr[0];
    	String message = new String();
    	for (int i = 1; i < msgArr.length; i++) {
    		message += msgArr[i] + " ";
    	}
    	
//    	"\"john\""
    	String json = "{ \"user\" : \"" + username + "\", \"message\" : \"" + message + "\" }";
    	
    	return json;
    }
    
    public WebSocketConnection getConnection() {
    	return _wsc;
    }
    
}
