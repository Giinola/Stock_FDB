package pkg.gestion_stock.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Configuration de la base de données
    private static final String URL = "jdbc:postgresql://postgresql-stock-fdb.alwaysdata.net:5432/stock-fdb_1";
    private static final String USER = "stock-fdb";
    private static final String PASSWORD = "FDB_BDD_STOCK";
    // Constructeur privé pour Singleton
    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion a la base de donnees reussie");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL non trouve");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de donnees");
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Reconnexion a la base de donnees");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la verification de la connexion");
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion a la base de donnees fermee");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion");
                e.printStackTrace();
            }
        }
    }
}
