package pkg.gestion_stock.service;

import pkg.gestion_stock.Database.DatabaseConnection;
import java.sql.*;

public class AuditService {

    private static AuditService instance = new AuditService();
    public static AuditService getInstance() { return instance; }

    public void log(String action, String tableCible, String detail) {
        String username = SessionManager.getInstance().getUsername();
        String sql = "INSERT INTO audit_log (username, action, table_cible, detail) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, action);
            stmt.setString(3, tableCible);
            stmt.setString(4, detail);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur audit : " + e.getMessage());
        }
    }
}



