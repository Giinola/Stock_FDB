package pkg.gestion_stock.dao;

import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    private DatabaseConnection dbConnection;

    public ProduitDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // ❌ PAS BESOIN de créer la table (elle existe déjà)
    // Mais on garde la méthode au cas où
    public void creerTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produit (
                id SERIAL PRIMARY KEY,
                categorie VARCHAR(100) NOT NULL,
                nom VARCHAR(100) NOT NULL,
                qty_in_stock DOUBLE PRECISION
            )
        """;

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Table 'produit' vérifiée");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la table 'produit'");
            e.printStackTrace();
        }
    }

    // Insérer un nouveau produit
    public void inserer(Produit produit) {
        String sql = "INSERT INTO produit (categorie, nom, qty_in_stock) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produit.getCategorie());
            pstmt.setString(2, produit.getNom());

            if (produit.getQtyInStock() != null) {
                pstmt.setDouble(3, produit.getQtyInStock());
            } else {
                pstmt.setNull(3, Types.DOUBLE);
            }

            pstmt.executeUpdate();
            System.out.println("✅ Produit inséré : " + produit.getNom());

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion du produit : " + produit.getNom());
            e.printStackTrace();
        }
    }

    // Sauvegarder (insert si n'existe pas, update sinon)
    public void sauvegarder(Produit produit) {
        // Vérifier si le produit existe déjà
        Produit existant = findByCategorieEtNom(produit.getCategorie(), produit.getNom());

        if (existant == null) {
            // Insérer
            inserer(produit);
        } else {
            // Mettre à jour
            mettreAJourQuantite(produit.getCategorie(), produit.getNom(), produit.getQtyInStock());
        }
    }

    // Sauvegarder plusieurs produits
    public void sauvegarderTous(List<Produit> produits) {
        for (Produit p : produits) {
            sauvegarder(p);
        }
        System.out.println("✅ " + produits.size() + " produits traités");
    }

    // Récupérer tous les produits
    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT categorie, nom, qty_in_stock FROM produit ORDER BY id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String categorie = rs.getString("categorie");
                String nom = rs.getString("nom");

                // Gérer les valeurs NULL pour qty_in_stock
                Double qty = null;
                double qtyValue = rs.getDouble("qty_in_stock");
                if (!rs.wasNull()) {
                    qty = qtyValue;
                }

                produits.add(new Produit(categorie, nom, qty));
            }

            System.out.println("✅ " + produits.size() + " produits chargés");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des produits");
            e.printStackTrace();
        }

        return produits;
    }

    // Trouver un produit par catégorie et nom
    public Produit findByCategorieEtNom(String categorie, String nom) {
        String sql = "SELECT categorie, nom, qty_in_stock FROM produit WHERE categorie = ? AND nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categorie);
            pstmt.setString(2, nom);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String cat = rs.getString("categorie");
                    String n = rs.getString("nom");

                    Double qty = null;
                    double qtyValue = rs.getDouble("qty_in_stock");
                    if (!rs.wasNull()) {
                        qty = qtyValue;
                    }

                    return new Produit(cat, n, qty);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du produit");
            e.printStackTrace();
        }

        return null;
    }

    // Mettre à jour la quantité d'un produit
    public void mettreAJourQuantite(String categorie, String nom, Double nouvelleQuantite) {
        String sql = "UPDATE produit SET qty_in_stock = ? WHERE categorie = ? AND nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (nouvelleQuantite != null) {
                pstmt.setDouble(1, nouvelleQuantite);
            } else {
                pstmt.setNull(1, Types.DOUBLE);
            }
            pstmt.setString(2, categorie);
            pstmt.setString(3, nom);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Quantité mise à jour : " + nom + " = " + nouvelleQuantite);
            } else {
                System.out.println("⚠️ Aucun produit trouvé pour : " + categorie + " - " + nom);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la quantité");
            e.printStackTrace();
        }
    }

    // Supprimer un produit
    public void supprimer(String categorie, String nom) {
        String sql = "DELETE FROM produit WHERE categorie = ? AND nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categorie);
            pstmt.setString(2, nom);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Produit supprimé (" + rowsAffected + " ligne(s))");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du produit");
            e.printStackTrace();
        }
    }

    // Compter le nombre de produits
    public int compter() {
        String sql = "SELECT COUNT(*) as count FROM produit";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des produits");
            e.printStackTrace();
        }

        return 0;
    }

    // Vérifier si la table est vide
    public boolean estVide() {
        return compter() == 0;
    }

    // Vider complètement la table (utile pour reset)
    public void viderTable() {
        String sql = "DELETE FROM produit";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("✅ Table vidée : " + rowsAffected + " produit(s) supprimé(s)");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du vidage de la table");
            e.printStackTrace();
        }
    }
}