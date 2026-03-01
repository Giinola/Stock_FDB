package pkg.gestion_stock.service;

import pkg.gestion_stock.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {
    private Connection connection;

    public AuthenticationService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        System.out.println("Service d'authentification initialise");
    }

    public boolean login(String username, String password) {
        try {
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                System.out.println("Champs vides");
                return false;
            }

            String query = "SELECT password, actif, compte_verrouille FROM utilisateur WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean actif = rs.getBoolean("actif");
                boolean verrouille = rs.getBoolean("compte_verrouille");

                if (!actif) {
                    System.out.println("Compte desactive : " + username);
                    rs.close(); stmt.close();
                    return false;
                }

                if (verrouille) {
                    System.out.println("Compte verrouille : " + username);
                    rs.close(); stmt.close();
                    return false;
                }

                if (storedPassword.equals(password)) {
                    System.out.println("Authentification reussie pour : " + username);

                    // Mise à jour dernier login
                    String updateQuery = "UPDATE utilisateur SET dernier_login = CURRENT_TIMESTAMP, tentatives_echec = 0 WHERE username = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    // ===== CHARGER LE ROLE =====
                    String roleQuery = "SELECT r.nom FROM role r " +
                            "JOIN utilisateur_role ur ON r.id = ur.role_id " +
                            "JOIN utilisateur u ON ur.utilisateur_id = u.id " +
                            "WHERE u.username = ?";
                    PreparedStatement roleStmt = connection.prepareStatement(roleQuery);
                    roleStmt.setString(1, username);
                    ResultSet roleRs = roleStmt.executeQuery();
                    String role = roleRs.next() ? roleRs.getString("nom") : "EMPLOYE";
                    roleStmt.close();

                    SessionManager.getInstance().login(username, role);

                    rs.close(); stmt.close();
                    return true;

                } else {
                    System.out.println("Mot de passe incorrect");

                    String updateQuery = "UPDATE utilisateur SET tentatives_echec = tentatives_echec + 1 WHERE username = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    rs.close(); stmt.close();
                    return false;
                }
            } else {
                System.out.println("Utilisateur inexistant : " + username);
                rs.close(); stmt.close();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    public boolean isAuthenticated() {
        return SessionManager.getInstance().isLoggedIn();
    }
}