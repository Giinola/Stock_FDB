package pkg.gestion_stock.service;

import pkg.gestion_stock.Database.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                    rs.close();
                    stmt.close();
                    return false;
                }

                if (verrouille) {
                    System.out.println("Compte verrouille : " + username);
                    rs.close();
                    stmt.close();
                    return false;
                }

                String hashedInputPassword = hashPassword(password);

                if (storedPassword.equals(hashedInputPassword)) {
                    System.out.println("Authentification reussie pour : " + username);

                    String updateQuery = "UPDATE utilisateur SET dernier_login = CURRENT_TIMESTAMP, tentatives_echec = 0 WHERE username = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    SessionManager.getInstance().login(username);

                    rs.close();
                    stmt.close();
                    return true;
                } else {
                    System.out.println("Mot de passe incorrect");

                    String updateQuery = "UPDATE utilisateur SET tentatives_echec = tentatives_echec + 1 WHERE username = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    rs.close();
                    stmt.close();
                    return false;
                }
            } else {
                System.out.println("Utilisateur inexistant : " + username);
                rs.close();
                stmt.close();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'authentification : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Erreur de hachage : " + e.getMessage());
            return password;
        }
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    public boolean isAuthenticated() {
        return SessionManager.getInstance().isLoggedIn();
    }
}
