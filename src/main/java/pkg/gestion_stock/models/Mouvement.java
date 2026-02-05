package pkg.gestion_stock.models;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mouvement {

    private String type;           // "ENTRÉE" ou "SORTIE"
    private String produitNom;     // Nom du produit
    private Double quantite;       // Quantité ajoutée/retirée
    private LocalDateTime date;    // Date et heure
    private String motif;          // Raison (optionnel)

    public Mouvement(String type, String produitNom, Double quantite, String motif) {
        this.type = type;
        this.produitNom = produitNom;
        this.quantite = quantite;
        this.date = LocalDateTime.now();
        this.motif = motif;
    }

    // Getters et Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProduitNom() {
        return produitNom;
    }

    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    // Méthode pour formater la date
    public String getDateFormatee() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }
}