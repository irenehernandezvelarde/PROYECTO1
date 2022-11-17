
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

public class Client  extends WebSocketClient {

    private boolean running = true;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int port = 8888;
        String host = "localhost";
        String location = "ws://" + host + ":" + port;
        String text = "";

        Client client = connecta(location);
        
        while (client.running) {
            text = sc.nextLine();

            try {
                client.send(text);
            } catch (WebsocketNotConnectedException e) {
                System.out.println("Conexión perdida...");
                client = connecta(location);
            }

            if (text.compareTo("exit") == 0) {
                client.running = false;
            }
        }

        if (client != null) { 
        	client.close(); 
        }
    }

    public Client (URI uri, Draft draft) {
        super (uri, draft);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Has recibido un mensaje: " + message);
        if (message.compareTo("exit") == 0) {
            System.out.println("El servidor se a parado");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Te has conectado a: " + getURI());
        
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Te has desconectado de: " + getURI());
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error con la conexión del socket");
    }

    static public Client connecta (String location) {
        Client client = null;

        try {
            client = new Client(new URI(location), (Draft) new Draft_6455());
            client.connect();
        } catch (URISyntaxException e) { 
            e.printStackTrace(); 
            System.out.println("Error: " + location + " no és una direcció URI de WebSocket vàlida");
        }

        return client;
    }
}
