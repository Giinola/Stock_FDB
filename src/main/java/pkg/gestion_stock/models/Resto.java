package pkg.gestion_stock.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Resto {

    private LocalDateTime date;
    private String categorie;
    private String produit;
    private Double quantite;

    public Resto(String categorie, String produit, Double quantite) {
        this.categorie = categorie;
        this.produit = produit;
        this.quantite = quantite;
        this.date = LocalDateTime.now();
    }

    // Getters
    public LocalDateTime getDate() { return date; }
    public String getCategorie() { return categorie; }
    public String getProduit() { return produit; }
    public Double getQuantite() { return quantite; }

    public String getDateFormatee() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // Setters
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }
}