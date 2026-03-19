package pkg.gestion_stock.models;

import javafx.beans.property.*;

public class RestoItem {

    private final StringProperty categorie;
    private final StringProperty produit;
    private final DoubleProperty total;
    private final BooleanProperty estTitreCategorie;

    public RestoItem(String categorie, String produit, Double total) {
        this.categorie = new SimpleStringProperty(categorie);
        this.produit = new SimpleStringProperty(produit);
        this.total = new SimpleDoubleProperty(total == null ? 0.0 : total);
        this.estTitreCategorie = new SimpleBooleanProperty(produit == null);
    }

    // Properties
    public StringProperty categorieProperty() { return categorie; }
    public StringProperty produitProperty() { return produit; }
    public DoubleProperty totalProperty() { return total; }
    public BooleanProperty estTitreCategorieProperty() { return estTitreCategorie; }

    // Getters
    public String getCategorie() { return categorie.get(); }
    public String getProduit() { return produit.get(); }
    public Double getTotal() { return total.get(); }
    public Boolean getEstTitreCategorie() { return estTitreCategorie.get(); }

    // Setters
    public void setTotal(Double value) {
        total.set(value == null ? 0.0 : value);
    }
}