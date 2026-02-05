package pkg.gestion_stock.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.models.Produit;
import javafx.scene.control.TableCell;
import pkg.gestion_stock.models.StockManager;

public class ProduitController {

    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colQuantite;

    private final ObservableList<Produit> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("=== DÉBUT INITIALIZE ===");

        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // Gestion propre de la quantité (null → vide)
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("qtyInStock"));
        colQuantite.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("—");  // ou "" si tu préfères vide
                } else {
                    setText(String.format("%.1f", item));  // 1 décimale, ou "%.0f" pour entier
                }
            }
        });

        // Style catégories (titre gras + fond gris)
        tableProduits.setRowFactory(tv -> new TableRow<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getNom() == null) {
                    setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-font-size: 13;");
                } else {
                    setStyle("");
                }
            }
        });

        tableProduits.setItems(data);
        tableProduits.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // colonnes s'adaptent

        chargerDonnees(); // charge au démarrage

        System.out.println("Nombre de produits chargés : " + data.size());
        System.out.println("=== FIN INITIALIZE ===");
    }

    // ✅ MODIFICATION ICI : Utiliser StockManager au lieu de créer les données localement
    private void chargerDonnees() {
        data.clear();
        data.addAll(StockManager.getInstance().getProduits());
    }

    @FXML
    private void actualiser() {
        chargerDonnees();
        System.out.println("✓ Actualisation effectuée");
    }
}