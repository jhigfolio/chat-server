import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HTTPResponse {
	
    public static void returnPage(String fileName, PrintWriter output, OutputStream os) throws InterruptedException {
        try {
        	
//            Path path = Paths.get(fileName.substring(1));
        	Path path = Paths.get(fileName);
            System.out.println(path);
            byte[] data = Files.readAllBytes(path);
            String header = "HTTP/1.1 200 OK\r\n" + "Content-Length: " + data.length + "\r\n \r\n";

            output.println(header);
            
            output.flush();
            os.write(data);

        } catch (IOException e) {
        	
        	String errMsg = "Error 404: File Not Fount";
            String header = "HTTP/1.1 200 OK\r\n" + "Content-Length: " + errMsg.length() + "\r\n \r\n";
            output.println(header);
            output.println(errMsg);
            
        }
        
    }
}
