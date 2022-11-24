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



// Compilar amb:
// javac -cp "lib/*:." WsServidor.java
// java -cp "lib/*:." WsServidor

// Tutorials: http://tootallnate.github.io/Java-WebSocket/

public class Server extends WebSocketServer {
    static Server socket;
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
<<<<<<< HEAD
    private ArrayList<ControlsBlock> controls = Model.getControls();
=======
    

    public static void connecta() throws InterruptedException, IOException {
        int port = 8888;
        boolean running = true;

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        socket = new Server(port);
        socket.start();
        System.out.println("WsServidor funciona al port: " + socket.getPort());

        
    }
>>>>>>> 1ebaea8edcb922788afbb5655c94ae3959cbc080

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

        // Saludem personalment al nou client
        //conn.send("Benvingut a WsServer");

        // Enviem la direcció URI del nou client a tothom
        //broadcast("Nova connexió: " + handshake.getResourceDescriptor());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
<<<<<<< HEAD
        System.out.println(host + " se a conectado");

        String modelToString = "model/";
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
        
        conn.send(modelToString);
=======
        System.out.println(host + " s'ha connectat");
>>>>>>> 1ebaea8edcb922788afbb5655c94ae3959cbc080
    }

    @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        // Informem a tothom que el client s'ha desconnectat
        //broadcast(conn + " s'ha desconnectat");

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println(conn + " s'ha desconnectat");
    }

    @Override public void onMessage(WebSocket conn, String message) {

        if (message.equalsIgnoreCase("getUsers")) {
            // Enviar la llsita de connexions al client
            HashMap<String, String> users = DataBase.getData();
            System.out.println(users.size());
            conn.send(objToBytes(users));
            System.out.println("Enviant usuaris");


        } else if (message.equalsIgnoreCase("getModel")) {
            conn.send(objToBytes(Model.getControls()));
        }
    }

    @Override public void onMessage(WebSocket conn, ByteBuffer message) {

        // Mostrem per pantalla (servidor) el missatge
        System.out.println(conn + ": " + message);
        
        String[] datos = (String[]) bytesToObject(message);
        if (datos.length == 2) { // Datos login
			String username = datos[0];
			String password = datos[1];
			conn.send(DataBase.checkLogin(username, password));
		} else if (datos.length == 4) {
			System.out.println("Datos recibidos");
            System.out.println(datos[0]);
            System.out.println(datos[1]);
            System.out.println(datos[2]);
            System.out.println(datos[3]);
			//interfazIndustry_2.updateInterfaz(datos);
		}
    }

    @Override public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

<<<<<<< HEAD
=======
    @Override
    public void onStart() {
        // S'inicia el servidor
        System.out.println("Escriu 'exit' per aturar el servidor");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

>>>>>>> 1ebaea8edcb922788afbb5655c94ae3959cbc080
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
            // Transforma el ByteButter en byte[]
            byte[] bytesArray = new byte[arr.remaining()];
            arr.get(bytesArray, 0, bytesArray.length);
  
            // Transforma l'array de bytes en objecte
            ByteArrayInputStream in = new ByteArrayInputStream(bytesArray);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
  
        } catch (ClassNotFoundException e) { e.printStackTrace();
        } catch (UnsupportedEncodingException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }
     
}
