import java.io.IOException;

public interface Listener {

    public void onMessage(String str) throws IOException;

    public void onClose() throws IOException;
    
    public WebSocketConnection getConnection();

}
