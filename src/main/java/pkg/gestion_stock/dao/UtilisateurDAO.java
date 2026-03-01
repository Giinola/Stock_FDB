package pkg.gestion_stock.dao;

import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    private Connection connection;

    public UtilisateurDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // ===== LISTER TOUS =====
    public List<Utilisateur> findAll() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT u.*, r.nom as role_nom FROM utilisateur u " +
                "LEFT JOIN utilisateur_role ur ON u.id = ur.utilisateur_id " +
                "LEFT JOIN role r ON ur.role_id = r.id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Utilisateur u = mapRow(rs);
                u.setRole(rs.getString("role_nom"));
                liste.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll : " + e.getMessage());
        }
        return liste;
    }

    // ===== AJOUTER =====
    public boolean insert(Utilisateur u, String roleNom) {
        String sql = "INSERT INTO utilisateur (username, password, nom, prenom, email, telephone, actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getNom());
            stmt.setString(4, u.getPrenom());
            stmt.setString(5, u.getEmail());
            stmt.setString(6, u.getTelephone());
            stmt.setBoolean(7, true);
            stmt.executeUpdate();

            // Récupérer l'ID généré et assigner le rôle
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                long userId = keys.getLong(1);
                assignerRole(userId, roleNom);
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur insert : " + e.getMessage());
            return false;
        }
    }

    // ===== MODIFIER =====
    public boolean update(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, telephone=?, actif=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getTelephone());
            stmt.setBoolean(5, u.isActif());
            stmt.setLong(6, u.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
            return false;
        }
    }

    // ===== SUPPRIMER =====
    public boolean delete(Long id) {
        // Supprimer d'abord le rôle lié
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM utilisateur_role WHERE utilisateur_id=?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur suppression role : " + e.getMessage());
        }

        String sql = "DELETE FROM utilisateur WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur delete : " + e.getMessage());
            return false;
        }
    }

    // ===== ASSIGNER ROLE =====
    private void assignerRole(long userId, String roleNom) {
        String sql = "INSERT INTO utilisateur_role (utilisateur_id, role_id) " +
                "SELECT ?, id FROM role WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, roleNom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur assignerRole : " + e.getMessage());
        }
    }

    // ===== MAPPER =====
    private Utilisateur mapRow(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setTelephone(rs.getString("telephone"));
        u.setActif(rs.getBoolean("actif"));
        return u;
    }
}