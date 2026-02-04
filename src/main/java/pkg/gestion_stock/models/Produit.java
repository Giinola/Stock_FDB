package pkg.gestion_stock.models;




public class Produit {

    private String categorie;
    private String nom;
    private Double qtyInStock;  // Quantité en stock (QTY IN STOCK)

    public Produit() {}

    public Produit(String categorie, String nom, Double qtyInStock) {
        this.categorie = categorie;
        this.nom = nom;
        this.qtyInStock = qtyInStock;
    }

    // Getters et Setters
    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getQtyInStock() {
        return qtyInStock;
    }

    public void setQtyInStock(Double qtyInStock) {
        this.qtyInStock = qtyInStock;
    }
}