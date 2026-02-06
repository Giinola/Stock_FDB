package pkg.gestion_stock.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.models.Mouvement;
import pkg.gestion_stock.models.Produit;
import pkg.gestion_stock.models.StockManager;

public class EntreeStockController {

    @FXML private TextField txtRecherche;
    @FXML private ComboBox<Produit> comboProduits;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtMotif;

    // ✅ NOUVEAUX LABELS POUR AFFICHER LES CALCULS
    @FXML private Label lblStockActuel;
    @FXML private Label lblNouveauStock;

    @FXML private TableView<Mouvement> tableMouvements;
    @FXML private TableColumn<Mouvement, String> colDate;
    @FXML private TableColumn<Mouvement, String> colProduit;
    @FXML private TableColumn<Mouvement, Double> colQuantite;
    @FXML private TableColumn<Mouvement, String> colMotif;

    private StockManager stockManager = StockManager.getInstance();
    private FilteredList<Produit> produitsFiltre;

    @FXML
    public void initialize() {
        System.out.println("✅ EntreeStockController initialisé");

        // Créer une liste filtrée des produits
        ObservableList<Produit> tousLesProduits = stockManager.getProduitsUniquement();
        produitsFiltre = new FilteredList<>(tousLesProduits, p -> true);

        // Charger les produits dans le ComboBox
        comboProduits.setItems(produitsFiltre);

        // Afficher le nom du produit dans le ComboBox
        comboProduits.setCellFactory(param -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCategorie() + " - " + item.getNom());
                }
            }
        });
        comboProduits.setButtonCell(new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCategorie() + " - " + item.getNom());
                }
            }
        });

        // ✅ LISTENER : Quand un produit est sélectionné, afficher son stock actuel
        comboProduits.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Double stockActuel = newValue.getQtyInStock();
                if (stockActuel == null) stockActuel = 0.0;
                lblStockActuel.setText(String.format("%.1f", stockActuel));
                calculerNouveauStock();
            } else {
                lblStockActuel.setText("—");
                lblNouveauStock.setText("—");
            }
        });

        // ✅ LISTENER : Quand la quantité change, calculer le nouveau stock
        txtQuantite.textProperty().addListener((observable, oldValue, newValue) -> {
            calculerNouveauStock();
        });

        // 🔍 BARRE DE RECHERCHE
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            produitsFiltre.setPredicate(produit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (produit.getNom() == null || produit.getCategorie() == null) {
                    return false;
                }

                String recherche = newValue.toLowerCase().trim();
                String nomProduit = produit.getNom().toLowerCase();
                String categorie = produit.getCategorie().toLowerCase();

                return nomProduit.contains(recherche) || categorie.contains(recherche);
            });

            if (produitsFiltre.size() == 1) {
                comboProduits.setValue(produitsFiltre.get(0));
            }
        });

        // Configuration de la table
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        tableMouvements.setItems(stockManager.getMouvements());
    }

    // ✅ CALCUL DU NOUVEAU STOCK EN TEMPS RÉEL
    private void calculerNouveauStock() {
        Produit produit = comboProduits.getValue();
        String quantiteText = txtQuantite.getText().trim();

        if (produit == null || quantiteText.isEmpty()) {
            lblNouveauStock.setText("—");
            return;
        }

        try {
            Double quantiteAjout = Double.parseDouble(quantiteText);
            Double stockActuel = produit.getQtyInStock();
            if (stockActuel == null) stockActuel = 0.0;

            Double nouveauStock = stockActuel + quantiteAjout;
            lblNouveauStock.setText(String.format("%.1f", nouveauStock));

            // Couleur verte si positif, rouge si négatif
            if (nouveauStock >= 0) {
                lblNouveauStock.setStyle("-fx-font-size: 18; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                lblNouveauStock.setStyle("-fx-font-size: 18; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }

        } catch (NumberFormatException e) {
            lblNouveauStock.setText("—");
        }
    }

    @FXML
    private void ajouterEntree() {
        Produit produitSelectionne = comboProduits.getValue();
        String quantiteText = txtQuantite.getText().trim();
        String motif = txtMotif.getText().trim();

        // Validation
        if (produitSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit.");
            return;
        }

        if (quantiteText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une quantité.");
            return;
        }

        try {
            Double quantite = Double.parseDouble(quantiteText);

            if (quantite <= 0) {
                showAlert("Erreur", "La quantité doit être positive.");
                return;
            }

            Double ancienStock = produitSelectionne.getQtyInStock();
            if (ancienStock == null) ancienStock = 0.0;

            // Ajouter l'entrée
            stockManager.ajouterEntree(produitSelectionne, quantite, motif);

            Double nouveauStock = produitSelectionne.getQtyInStock();

            // Réinitialiser le formulaire
            txtRecherche.clear();
            comboProduits.setValue(null);
            txtQuantite.clear();
            txtMotif.clear();
            lblStockActuel.setText("—");
            lblNouveauStock.setText("—");

            showAlert("Succès", String.format(
                    "✅ Entrée ajoutée avec succès !\n\n" +
                            "Produit : %s\n" +
                            "Ancien stock : %.1f\n" +
                            "Quantité ajoutée : +%.1f\n" +
                            "Nouveau stock : %.1f",
                    produitSelectionne.getNom(), ancienStock, quantite, nouveauStock
            ));

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La quantité doit être un nombre valide.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
