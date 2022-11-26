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
    private ArrayList<ControlsBlock> controls = Model.getControls();

    

    public static void connecta() throws InterruptedException, IOException {
        int port = 8888;
        boolean running = true;

        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        socket = new Server(port);
        socket.start();
        System.out.println("WsServidor funciona al port: " + socket.getPort());
    }

    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " s'ha connectat");
        String modelToString = "model/";
        //Formacio del missatge (representacio string del model preparada per a fer .split())
        ArrayList<ControlsBlock> controls = Model.getControls();
        if (controls == null){conn.send("SERVER ERROR/No_model_loaded");return;}
        for (int a = 0; a < controls.size(); a++){//For each block
            ControlsBlock block = controls.get(a);
            modelToString += block.toString();
            modelToString += ";";
            for (int b = 0; b < block.size(); b++){//For each component
                modelToString += block.get(b).toString();
                modelToString += "!";
            }
            modelToString += "#";
        }
        System.out.println(modelToString);
        //Enviament model
        conn.send(modelToString);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        System.out.println(conn + " s'ha desconnectat");
    }


    //USAR ESTE CON OTRA CONDICION AL RECIBIR STRING QUE EMPIECE POR "CAMBIO" O CHANGE, SLICE, SPLIT Y SACAR LOS VALORES DE ID ELEMENTO Y VALUE
    @Override
    public void onMessage(WebSocket conn, String message) {

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

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

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
		}
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        // S'inicia el servidor
        System.out.println("Escriu 'exit' per aturar el servidor");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
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
