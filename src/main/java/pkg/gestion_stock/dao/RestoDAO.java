package pkg.gestion_stock.dao;

import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.Resto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestoDAO {

    public void creerTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS resto (
                id SERIAL PRIMARY KEY,
                date TIMESTAMP NOT NULL,
                categorie VARCHAR(100) NOT NULL,
                produit VARCHAR(100) NOT NULL,
                quantite DOUBLE PRECISION NOT NULL
            )
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Table resto créée");

        } catch (SQLException e) {
            System.err.println("❌ Erreur création table resto : " + e.getMessage());
        }
    }

    public void sauvegarderComptage(List<Resto> comptages, LocalDateTime date) {
        String sql = "INSERT INTO resto (date, categorie, produit, quantite) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Resto r : comptages) {
                pstmt.setTimestamp(1, Timestamp.valueOf(date));
                pstmt.setString(2, r.getCategorie());
                pstmt.setString(3, r.getProduit());
                pstmt.setDouble(4, r.getQuantite());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            System.out.println("✅ Comptage RESTO sauvegardé : " + comptages.size() + " produits");

        } catch (SQLException e) {
            System.err.println("❌ Erreur sauvegarde comptage RESTO : " + e.getMessage());
        }
    }

    // ✅ Tronquer à la minute pour éviter les problèmes de secondes
    public List<LocalDateTime> findDatesComptage() {
        List<LocalDateTime> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE_TRUNC('minute', date) as date FROM resto ORDER BY date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dates.add(rs.getTimestamp("date").toLocalDateTime());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture dates : " + e.getMessage());
        }

        return dates;
    }

    // ✅ Comparer sur la minute, pas sur la seconde exacte
    public List<Resto> findByDate(LocalDateTime date) {
        List<Resto> comptages = new ArrayList<>();
        String sql = "SELECT * FROM resto WHERE DATE_TRUNC('minute', date) = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Resto r = new Resto(
                        rs.getString("categorie"),
                        rs.getString("produit"),
                        rs.getDouble("quantite")
                );
                r.setDate(rs.getTimestamp("date").toLocalDateTime());
                comptages.add(r);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture comptage : " + e.getMessage());
        }

        return comptages;
    }

    public void supprimerComptage(LocalDateTime date) {
        // ✅ Supprimer aussi par troncature à la minute
        String sql = "DELETE FROM resto WHERE DATE_TRUNC('minute', date) = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(date));
            pstmt.executeUpdate();
            System.out.println("✅ Comptage supprimé");

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
        }
    }
}