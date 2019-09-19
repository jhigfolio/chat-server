import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class WebSocketConnection {

    private OutputStream os;
    private Socket client;
    private Listener listener;

    WebSocketConnection(Socket soc) throws IOException{
    	client = soc;
    	listener = new ChatClient(this);
//    	listener = new EchoClient(this);
    }

	public void handshakeWS(String keyValue, PrintWriter output) throws NoSuchAlgorithmException {
	    keyValue +="258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	    MessageDigest md= MessageDigest.getInstance("SHA-1");
	    byte[]messageDigest = md.digest(keyValue.getBytes());
	    String hashedString = Base64.getEncoder().encodeToString(messageDigest);
	
	    String header="HTTP/1.1 101 Switching Protocols\r\n"
	                +"Upgrade: websocket\r\n"
	                +"Connection: Upgrade\r\n"
	                +"Sec-WebSocket-Accept: " + hashedString + "\r\n \r\n";
	
	    output.println(header);
	    output.flush();
	
	}
	
    //reading messages from the client
    public void listen() throws IOException {
    	InputStream ins = client.getInputStream();
        os = client.getOutputStream();

        while(true) {
           readFromClient(ins);
        }

    }
    
    public void readFromClient(InputStream ins) throws IOException {
        int bitMask=0xff;
        int bitMask2=0xF;
        
    	 byte[] header=new byte[2];
         ins.read(header);

         if((header[0] >> 4 & bitMask2)!=8) {
             client.close();
         }

         if((header[0] >> 7 & 0x1)!=1) {
             client.close();
         }

         // int length=header[1]&bitMask;
         int length = header[1];
             length&=bitMask; 
             length-=128;

             if (length == 126) {
                 // read extra 2 bytes here and they become the length
                 byte[] extra = new byte[2];
                 ins.read(extra);
                 int newLength = (extra[0] << 8) | extra[1];
                 length = newLength;
             } else if (length == 127) {
                 // same deal here
             }

         //byte array to store the 4 bytes that represent the key
         byte[] key=new byte[4];
         ins.read(key);

         //byte array to store the encoded message
         byte[] encoded=new byte[length];
         ins.read(encoded);

         //byte array that will store the decoded message
         byte[] decoded=new byte[length];

         for (int i = 0; i < encoded.length; i++) {
             decoded[i] = (byte)(encoded[i] ^ key[i & 0x3]);
         }

         String message = new String (decoded, "US-ASCII");
         listener.onMessage(message);
         
    }

     public void sendMessage(String msg) throws IOException {
    	 
    	 System.out.println("sending message: "+ msg);

        byte[] header=new byte[2];
        header[0]=(byte) ((0x8<<4) | 0x1);
        header[1]=(byte) msg.length();
        byte[] message = msg.getBytes();

        os.write(header);
        os.write(message);
        os.flush();

    }

}
