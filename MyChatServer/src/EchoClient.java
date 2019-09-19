import java.io.IOException;

// Client will need access to the room and its websocket connection
public class EchoClient implements Listener {

    private WebSocketConnection _wsc;
    private Room _room = null;
    
    EchoClient (WebSocketConnection wsc) {
    	_wsc = wsc;
    }

    //reading messages from the client
    public void onMessage(String msg) throws IOException {
    	
    	String[] parsedMsg = msg.split("\\s");
    	
    	if (parsedMsg[0].equals("join")) {
    		System.out.println("room joined: " + parsedMsg[1]);
    		_room = Room.getRoom(parsedMsg[1]);
    		System.out.println(_room);
    		_room.addUser(this);
    		_room.printUsers();
    	} else {
    		  // Send message back to client.
    		System.out.println(_room);
    		if (_room != null) {
    			_room.broadcast(msg);
    		} else {
    			_wsc.sendMessage("Hey, join a room before posting messages");
    		}

    	}

    }
    
    public void onClose() throws IOException {
        System.out.println("connection closed");

    }
    
    public WebSocketConnection getConnection() {
    	return _wsc;
    }
    
}
