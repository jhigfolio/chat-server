import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class HTTPRequest {

    public static Socket createClient(ServerSocket y) throws IOException {
        Socket client=y.accept();
        return client; 
    }


    public static Scanner createScanner (Socket y) throws IOException {
        Scanner s =new Scanner (y.getInputStream()); 
        return s; 
    }


    public static String getRequest (Scanner s, Socket y) throws BadRequestException {
        HashMap <String,String> map=new HashMap<String,String>();
        String input=s.nextLine();
        while (true) {
        String str= s.nextLine();
        
        if (str.equals("")) {
        	break;
        }

        String[] parsedReq=str.split(": ");
        String key=parsedReq[0];
        String val=parsedReq[1];
        map.put(key,val);

        }

        String upgradeVal=map.get("Upgrade");

        if (upgradeVal!=null) {
	        String requestKey=map.get("Sec-WebSocket-Key");
	        // switch to bool based IsWebSocketRequest
	        return "upgrade "+ requestKey;
        }

        String[] result = input.split("\\s");
        
        if (!result[0].equals("GET")) {
            throw new BadRequestException();
        } if(result[1].equals("/")) {
        	 String filename = "resources/index.html";
 	        return filename; 
        } else {
	        String filename = "resources/" + result[1];
	        return filename; 
        }
    }

}
