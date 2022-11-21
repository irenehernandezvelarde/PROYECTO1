
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.password4j.Password;

public class DataBase {

    private static String basePath = System.getProperty("user.dir") + "/";
    private static String filePath = basePath + "database.db";
    private static String pathSalt = basePath + "salt.db";
    private static String pathPepper = basePath + "pepper.db";

    public static void main(String[] args) throws SQLException {

        // Si no hi ha l'arxiu creat, el crea i li posa dades
    	System.out.println(basePath);
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) {
            initDatabase();
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

    static void initDatabase() {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);
        Connection connSalt = UtilsSQLite.connect(pathSalt);
        Connection connPepper = UtilsSQLite.connect(pathPepper);

        // Esborrar la taula (per si existeix)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS users;");

        // Crear una nova taula
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS users ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	username text NOT NULL,"
                + " password text NOT NULL);");
        
        UtilsSQLite.queryUpdate(connSalt, "DROP TABLE IF EXISTS salt;");
        UtilsSQLite.queryUpdate(connSalt, "CREATE TABLE IF NOT EXISTS salt("
        		+ "id integer PRIMARY KEY AUTOINCREMENT,"
        		+ "idUser integer NOT NULL,"
        		+ "salt text NOT NULL);");
        
        UtilsSQLite.queryUpdate(connPepper, "DROP TABLE IF EXISTS pepper;");
        UtilsSQLite.queryUpdate(connPepper, "CREATE TABLE IF NOT EXISTS pepper("
        		+ "id integer PRIMARY KEY AUTOINCREMENT,"
        		+ "idUser integer NOT NULL,"
        		+ "pepper text NOT NULL);");

        // Afegir elements a una taula
        
        
        String password = "Irene";
        String passwordSalt = "prueba";
        String passwordPepper = "pimienta";
        
       
        String hash = Password.hash(password).addSalt(passwordSalt).addPepper(passwordPepper).withArgon2().getResult();
        
        UtilsSQLite.queryUpdate(conn, "INSERT INTO users (username,password) VALUES (\"test\",'"+hash+"');");

        ResultSet rs = UtilsSQLite.querySelect(conn, "select id from users where username = 'test';");
        int id = 0;
        try {
			while (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Id: "+id);
        
        UtilsSQLite.queryUpdate(connSalt, "insert into salt (idUser,salt) values ("+id+",'"+passwordSalt+"');");
        UtilsSQLite.queryUpdate(connPepper, "insert into pepper (idUser,pepper) values ("+id+",'"+passwordPepper+"');");
        
        
        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
    
    public static String checkLogin(String username, String password) {
    	Connection conn = UtilsSQLite.connect(filePath);
        Connection connSalt = UtilsSQLite.connect(pathSalt);
        Connection connPepper = UtilsSQLite.connect(pathPepper);
        
        ResultSet rs = UtilsSQLite.querySelect(conn, "select id,password from users where username = '"+username+"';");
        int idUser = 0;
        String passwordHash = "";
        try {
			rs.next();
			idUser = rs.getInt(1);
			passwordHash = rs.getString(2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(idUser);
        
        ResultSet rsSalt = UtilsSQLite.querySelect(connSalt, "select salt from salt where idUser = "+idUser+";");
        String passwordSalt = "";
        try {
			rsSalt.next();
			passwordSalt = rsSalt.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(passwordSalt);
        
        ResultSet rsPepper = UtilsSQLite.querySelect(connPepper, "select pepper from pepper where idUser = "+idUser+";");
        String passwordPepper = "";
        try {
			rsPepper.next();
			passwordPepper = rsPepper.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(passwordPepper);
        boolean check;
        
        if (passwordSalt != null && passwordSalt != null && passwordPepper != null) {
	        check = Password.check(password, passwordHash).addSalt(passwordSalt).addPepper(passwordPepper).withArgon2();
		} else {
			check = false;
		}
        
        System.out.println("Login: " + check);
        if (check) {
			return "true";
		} else {
			return "false";
		}
    }
}
