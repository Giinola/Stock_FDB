package pkg.gestion_stock.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.models.Mouvement;
import pkg.gestion_stock.models.Produit;
import pkg.gestion_stock.models.StockManager;

public class EntreeStockController {

    @FXML private ComboBox<Produit> comboProduits;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtMotif;

    @FXML private TableView<Mouvement> tableMouvements;
    @FXML private TableColumn<Mouvement, String> colDate;
    @FXML private TableColumn<Mouvement, String> colProduit;
    @FXML private TableColumn<Mouvement, Double> colQuantite;
    @FXML private TableColumn<Mouvement, String> colMotif;

    private StockManager stockManager = StockManager.getInstance();

    @FXML
    public void initialize() {
        System.out.println("✅ EntreeStockController initialisé");

        // Charger les produits dans le ComboBox
        comboProduits.setItems(stockManager.getProduitsUniquement());

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

        // Configuration de la table
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        // Lier la table aux mouvements
        tableMouvements.setItems(stockManager.getMouvements());
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

            // Ajouter l'entrée
            stockManager.ajouterEntree(produitSelectionne, quantite, motif);

            // Réinitialiser le formulaire
            comboProduits.setValue(null);
            txtQuantite.clear();
            txtMotif.clear();

            showAlert("Succès", "Entrée ajoutée avec succès !\n"
                    + produitSelectionne.getNom() + " : +" + quantite);

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