import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, BadRequestException {
		
		System.out.println("Server listening on port 8080...");

		// establishing ServerSocket on port 8080
		ServerSocket server = new ServerSocket(8080);

		while (true) {
			// waiting to create client for each connection
			Socket client = HTTPRequest.createClient(server);

			Thread t = new Thread(() -> {
				try {
					// scanner to read in input from client
					Scanner s = HTTPRequest.createScanner(client);

					PrintWriter output = new PrintWriter(client.getOutputStream(), true);
					OutputStream os = client.getOutputStream();

					// this string will contain the line entered
					String filename = HTTPRequest.getRequest(s, client);
//					System.out.println("file: " + filename);
					String[] parsedFile =filename.split("\\s");
					
					if (parsedFile[0].equals("upgrade")){
//						System.out.println("WEB SOCKET");
						String requestKey=parsedFile[1];
						WebSocketConnection wsc = new WebSocketConnection(client);
						wsc.handshakeWS(requestKey, output);
						wsc.listen();
//						wsc.close();
						output.flush();
						
					} else {
						HTTPResponse.returnPage(filename, output, os);
						output.flush();
						client.close();
						s.close();
						output.close();
						os.close();
					}
				} catch (BadRequestException e) {
					try {
						PrintWriter output = new PrintWriter(client.getOutputStream(), true);
						String header = "HTTP/1.1 200 OK\r\n" + "Content-Length: " + 15 + "\r\n \r\n";
						output.println(header);
						output.println("400 BAD REQUEST");

					} catch (IOException io) {
						io.printStackTrace();
					}
				} catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
					e.printStackTrace();
				} 

			});
			
			t.start();

		}
	}
}
