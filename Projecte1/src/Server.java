
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class Server extends WebSocketServer {
    static Server socket;
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<ControlsBlock> controls = Model.getControls();

    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public Server(InetSocketAddress address) {
        super(address);
    }

    public static void connecta() throws InterruptedException, IOException {
        int port = 8888;
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        socket = new Server(port);
        socket.start();
        System.out.println("Servidor funciona al port: " + socket.getPort());
    }
    
    @Override public void onStart() {
    	
        System.out.println("Escriu 'exit' per aturar el servidor");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //DADES DE LA NOVA CONNEXIO
        conn.send("Bienvenido a IETI Industry");
        broadcast("Nueva conexión: " + handshake.getResourceDescriptor());
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " se a conectado");
        
        //SINCRONITZACIO INICIAL DEL MODEL (ENVIAMENT MODEL A NOVA CONNEXIO)
        //Key
        String modelToString = "model/";
        //Formacio del missatge (representacio string del model preparada per a fer .split())
        ArrayList<ControlsBlock> controls = Model.getControls();
        for (int a = 0; a < controls.size(); a++){//For each block
            ControlsBlock block = controls.get(a);
            modelToString += "#";
            modelToString += block.toString();
            for (int b = 0; b < block.size(); b++){//For each component
                modelToString += "*";
                modelToString += block.get(b).toString();
            }
        }
        System.out.println(modelToString);
        //Enviament model
        conn.send(modelToString);
    }

    @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        broadcast(conn + " se a desconectado");
        System.out.println(conn + " se a desconectado");
    }

    @Override public void onMessage(WebSocket conn, String message) {

        if (message.equalsIgnoreCase("getUsers")) {
        	
            HashMap<String, String> users = DataBase.getData();
            System.out.println(users.size());
            conn.send(objToBytes(users));
            System.out.println("Enviant usuaris" + users);


        } else if (message.equalsIgnoreCase("getModel")) {
            conn.send(objToBytes(Model.getControls()));
        }
    }

    @Override public void onMessage(WebSocket conn, ByteBuffer message) {

        // Mostrem per pantalla (servidor) el missatge
        System.out.println(conn + ": " + message);
    }

    @Override public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public String getConnectionId(WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public static byte[] objToBytes (Object obj) {
        byte[] result = null;
        try {
            // Transforma l'objecte a bytes[]
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            result = bos.toByteArray();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }
  
    public static Object bytesToObject (ByteBuffer arr) {
        Object result = null;
        try {
            byte[] bytesArray = new byte[arr.remaining()];
            arr.get(bytesArray, 0, bytesArray.length);
  
            ByteArrayInputStream in = new ByteArrayInputStream(bytesArray);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
  
        } catch (ClassNotFoundException e) { e.printStackTrace();
        } catch (UnsupportedEncodingException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }
  
}
