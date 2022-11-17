import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {

    private static String basePath = System.getProperty("user.dir") + "/";
    private static String filePath = basePath + "database.db";

    public static void main(String[] args) throws SQLException {

        // Si no hi ha l'arxiu creat, el crea i li posa dades
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) {
            initDatabase(filePath);
        }

    }

    static HashMap<String, String> getData() {
        Connection conn = UtilsSQLite.connect(filePath);
        HashMap<String, String> data = new HashMap<String, String>();
        ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT username, password FROM users");

        try {
            while (rs.next()) {
                data.put(rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;

    }

    static void initDatabase(String filePath) {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar la taula (per si existeix)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS users;");

        // Crear una nova taula
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS users ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	username text NOT NULL,"
                + " password text NOT NULL);");

        // Afegir elements a una taula
        UtilsSQLite.queryUpdate(conn, "INSERT INTO users (username,password) VALUES (\"Neei\",\"1234\");");

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
}