package pkg.gestion_stock.dao;

import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    // On appelle getConnection() à chaque fois — DatabaseConnection gère la reconnexion
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void creerTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produit (
                id SERIAL PRIMARY KEY,
                categorie VARCHAR(100) NOT NULL,
                nom VARCHAR(100) NOT NULL,
                qty_in_stock DOUBLE PRECISION
            )
        """;
        try (Statement stmt = conn().createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Table 'produit' vérifiée");
        } catch (SQLException e) {
            System.err.println("❌ Erreur table : " + e.getMessage());
        }
    }

    public void inserer(Produit produit) {
        String sql = "INSERT INTO produit (categorie, nom, qty_in_stock) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, produit.getCategorie());
            pstmt.setString(2, produit.getNom());
            if (produit.getQtyInStock() != null) pstmt.setDouble(3, produit.getQtyInStock());
            else pstmt.setNull(3, Types.DOUBLE);
            pstmt.executeUpdate();
            System.out.println("✅ Produit inséré : " + produit.getNom());
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion : " + e.getMessage());
        }
    }

    public void sauvegarder(Produit produit) {
        Produit existant = findByCategorieEtNom(produit.getCategorie(), produit.getNom());
        if (existant == null) inserer(produit);
        else mettreAJourQuantite(produit.getCategorie(), produit.getNom(), produit.getQtyInStock());
    }

    public void sauvegarderTous(List<Produit> produits) {
        for (Produit p : produits) sauvegarder(p);
        System.out.println("✅ " + produits.size() + " produits traités");
    }

    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT categorie, nom, qty_in_stock FROM produit ORDER BY id";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Double qty = null;
                double qtyValue = rs.getDouble("qty_in_stock");
                if (!rs.wasNull()) qty = qtyValue;
                produits.add(new Produit(rs.getString("categorie"), rs.getString("nom"), qty));
            }
            System.out.println("✅ " + produits.size() + " produits chargés");
        } catch (SQLException e) {
            System.err.println("❌ Erreur findAll : " + e.getMessage());
        }
        return produits;
    }

    public Produit findByCategorieEtNom(String categorie, String nom) {
        String sql = "SELECT categorie, nom, qty_in_stock FROM produit WHERE categorie = ? AND nom = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, categorie);
            pstmt.setString(2, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Double qty = null;
                    double qtyValue = rs.getDouble("qty_in_stock");
                    if (!rs.wasNull()) qty = qtyValue;
                    return new Produit(rs.getString("categorie"), rs.getString("nom"), qty);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByCategorieEtNom : " + e.getMessage());
        }
        return null;
    }

    public void mettreAJourQuantite(String categorie, String nom, Double nouvelleQuantite) {
        String sql = "UPDATE produit SET qty_in_stock = ? WHERE categorie = ? AND nom = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            if (nouvelleQuantite != null) pstmt.setDouble(1, nouvelleQuantite);
            else pstmt.setNull(1, Types.DOUBLE);
            pstmt.setString(2, categorie);
            pstmt.setString(3, nom);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("✅ Quantité mise à jour : " + nom);
            else System.out.println("⚠️ Produit non trouvé : " + categorie + " - " + nom);
        } catch (SQLException e) {
            System.err.println("❌ Erreur update : " + e.getMessage());
        }
    }

    public void supprimer(String categorie, String nom) {
        String sql = "DELETE FROM produit WHERE categorie = ? AND nom = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, categorie);
            pstmt.setString(2, nom);
            int rows = pstmt.executeUpdate();
            System.out.println("✅ Produit supprimé (" + rows + " ligne(s))");
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
        }
    }

    public int compter() {
        String sql = "SELECT COUNT(*) as count FROM produit";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) {
            System.err.println("❌ Erreur compter : " + e.getMessage());
        }
        return 0;
    }

    public boolean estVide() {
        return compter() == 0;
    }

    public void viderTable() {
        String sql = "DELETE FROM produit";
        try (Statement stmt = conn().createStatement()) {
            int rows = stmt.executeUpdate(sql);
            System.out.println("✅ Table vidée : " + rows + " produit(s) supprimé(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur viderTable : " + e.getMessage());
        }
    }
}