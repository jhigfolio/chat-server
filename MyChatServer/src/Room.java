import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// Room will create a room object and keep track of all the clients in the room
// get room will loop through map and see if the room exists, 
// if it doesn't then create the room and put it in the hash map
public class Room {
	
	// a list of all rooms
	public static HashMap <String, Room> rooms = new HashMap<String, Room>();

    private ArrayList <Listener> users = new ArrayList<Listener>(); // clients in room
    private ArrayList <String> messages = new ArrayList<String>();
     
    public static synchronized Room getRoom(String name) {

    	if (rooms.containsKey(name)) {
//    		System.out.println("room exists: " + name);
    		return rooms.get(name);
    	} else {
    		System.out.println("new room: " + name);
    		Room newRoom = new Room();
    		rooms.put(name, newRoom);
    		return newRoom;
    	}
    	
    }
    
    public synchronized void addUser(Listener user) throws IOException {
    	populateMessageHistory(user);
    	if (!users.contains(user)) {
    		users.add(user);
    	} 
//    	else {
//    		System.out.println("user is already in room");
//    	}
    }
    
    // FOR TESTING
    public void printUsers() {
    	if (users != null) {
        	for (Listener usr : users) {
//        		System.out.println("user: " + usr);
        	}
    	}
    }
    
    public void broadcast(String msg) throws IOException {
    	if (users != null) {
    		messages.add(msg);
        	for (Listener usr : users) {
        		WebSocketConnection wsc = usr.getConnection();
        		try {
        			wsc.sendMessage(msg);
        		} catch (Exception e) {
//        			System.out.println("client left....");
//        			users.remove(usr);
        		}
        	}
    	}
    }
    
    public void populateMessageHistory(Listener user) throws IOException {
    	WebSocketConnection wsc = user.getConnection();
    	for (String msg : messages) {
    		wsc.sendMessage(msg);
    	}
    }

}
