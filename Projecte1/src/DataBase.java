import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;


public class DataBase {

    public static void main(String[] args) throws SQLException {
        String basePath = System.getProperty("user.dir") + "/";
        String filePath = basePath + "database.db";

        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) { initDatabase(filePath); }
        Connection conn = UtilsSQLite.connect(filePath);
        UtilsSQLite.disconnect(conn);
    }

    static void initDatabase (String filePath) {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar la taula (per si existeix)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS userCredentials;");

        // Crear una nova taula
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS userCredentials ("
                                    + "	id integer PRIMARY KEY AUTOINCREMENT,"
                                    + "	name string NOT NULL"
                                    + " password string NOT NULL);");

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
}