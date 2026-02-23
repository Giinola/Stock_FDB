package pkg.gestion_stock.dao;


import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.Mouvement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MouvementStockDAO {

    private DatabaseConnection dbConnection;

    public MouvementStockDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // Créer la table mouvements si elle n'existe pas
    public void creerTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS mouvement (
                id SERIAL PRIMARY KEY,
                type VARCHAR(50) NOT NULL,
                produit_nom VARCHAR(100) NOT NULL,
                quantite DOUBLE PRECISION NOT NULL,
                motif TEXT,
                date TIMESTAMP NOT NULL
            )
        """;

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Table 'mouvement' vérifiée");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la table 'mouvement'");
            e.printStackTrace();
        }
    }

    // Insérer un mouvement
    public void sauvegarder(Mouvement mouvement) {
        String sql = "INSERT INTO mouvement (type, produit_nom, quantite, motif, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mouvement.getType());
            pstmt.setString(2, mouvement.getProduitNom());
            pstmt.setDouble(3, mouvement.getQuantite());
            pstmt.setString(4, mouvement.getMotif());
            pstmt.setTimestamp(5, Timestamp.valueOf(mouvement.getDate()));

            pstmt.executeUpdate();
            System.out.println("✅ Mouvement sauvegardé : " + mouvement.getType() + " " + mouvement.getProduitNom());

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la sauvegarde du mouvement");
            e.printStackTrace();
        }
    }

    // Récupérer tous les mouvements
    public List<Mouvement> findAll() {
        List<Mouvement> mouvements = new ArrayList<>();
        String sql = "SELECT type, produit_nom, quantite, motif, date FROM mouvement ORDER BY date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                String produitNom = rs.getString("produit_nom");
                Double quantite = rs.getDouble("quantite");
                String motif = rs.getString("motif");
                Timestamp timestamp = rs.getTimestamp("date");

                Mouvement mouvement = new Mouvement(type, produitNom, quantite, motif);
                mouvement.setDate(timestamp.toLocalDateTime());

                mouvements.add(mouvement);
            }

            System.out.println("✅ " + mouvements.size() + " mouvements chargés");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des mouvements");
            e.printStackTrace();
        }

        return mouvements;
    }

    // Récupérer les mouvements d'un produit spécifique
    public List<Mouvement> findByProduit(String produitNom) {
        List<Mouvement> mouvements = new ArrayList<>();
        String sql = "SELECT type, produit_nom, quantite, motif, date FROM mouvement WHERE produit_nom = ? ORDER BY date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produitNom);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    String nom = rs.getString("produit_nom");
                    Double quantite = rs.getDouble("quantite");
                    String motif = rs.getString("motif");
                    Timestamp timestamp = rs.getTimestamp("date");

                    Mouvement mouvement = new Mouvement(type, nom, quantite, motif);
                    mouvement.setDate(timestamp.toLocalDateTime());

                    mouvements.add(mouvement);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des mouvements du produit");
            e.printStackTrace();
        }

        return mouvements;
    }

    // Récupérer les mouvements par type (ENTRÉE ou SORTIE)
    public List<Mouvement> findByType(String type) {
        List<Mouvement> mouvements = new ArrayList<>();
        String sql = "SELECT type, produit_nom, quantite, motif, date FROM mouvement WHERE type = ? ORDER BY date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String t = rs.getString("type");
                    String nom = rs.getString("produit_nom");
                    Double quantite = rs.getDouble("quantite");
                    String motif = rs.getString("motif");
                    Timestamp timestamp = rs.getTimestamp("date");

                    Mouvement mouvement = new Mouvement(t, nom, quantite, motif);
                    mouvement.setDate(timestamp.toLocalDateTime());

                    mouvements.add(mouvement);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des mouvements par type");
            e.printStackTrace();
        }

        return mouvements;
    }

    // Supprimer tous les mouvements d'un produit
    public void supprimerParProduit(String produitNom) {
        String sql = "DELETE FROM mouvement WHERE produit_nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produitNom);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ " + rowsAffected + " mouvement(s) supprimé(s)");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression des mouvements");
            e.printStackTrace();
        }
    }
}