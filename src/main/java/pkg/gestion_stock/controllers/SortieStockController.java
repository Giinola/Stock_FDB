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

import java.time.LocalDate;

public class SortieStockController {

    // ===== FORMULAIRE =====
    @FXML private TextField txtRecherche;
    @FXML private ComboBox<Produit> comboProduits;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtMotif;
    @FXML private Label lblStockActuel;
    @FXML private Label lblNouveauStock;

    // ===== FILTRES =====
    @FXML private ComboBox<String> comboFiltreType;
    @FXML private DatePicker datePickerFiltre;
    @FXML private TextField txtFiltreRecherche;

    // ===== TABLEAU =====
    @FXML private TableView<Mouvement> tableMouvements;
    @FXML private TableColumn<Mouvement, String> colDate;
    @FXML private TableColumn<Mouvement, String> colType;
    @FXML private TableColumn<Mouvement, String> colProduit;
    @FXML private TableColumn<Mouvement, Double> colQuantite;
    @FXML private TableColumn<Mouvement, String> colMotif;

    private StockManager stockManager = StockManager.getInstance();
    private FilteredList<Produit> produitsFiltre;
    private FilteredList<Mouvement> mouvementsFiltre;

    @FXML
    public void initialize() {
        System.out.println("✅ SortieStockController initialisé");

        // ===== COMBO PRODUITS =====
        ObservableList<Produit> tousLesProduits = stockManager.getProduitsUniquement();
        produitsFiltre = new FilteredList<>(tousLesProduits, p -> true);
        comboProduits.setItems(produitsFiltre);

        comboProduits.setCellFactory(param -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getCategorie() + " — " + item.getNom());
            }
        });
        comboProduits.setButtonCell(new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getCategorie() + " — " + item.getNom());
            }
        });

        // Listener sélection produit → affichage stock actuel
        comboProduits.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                double stock = newVal.getQtyInStock() == null ? 0.0 : newVal.getQtyInStock();
                lblStockActuel.setText(String.format("%.1f", stock));
                calculerNouveauStock();
            } else {
                lblStockActuel.setText("—");
                lblNouveauStock.setText("—");
                lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            }
        });

        // Listener quantité → calcul en temps réel
        txtQuantite.textProperty().addListener((obs, o, n) -> calculerNouveauStock());

        // Barre de recherche produit (formulaire)
        txtRecherche.textProperty().addListener((obs, o, newValue) -> {
            produitsFiltre.setPredicate(produit -> {
                if (newValue == null || newValue.isEmpty()) return true;
                if (produit.getNom() == null || produit.getCategorie() == null) return false;
                String r = newValue.toLowerCase().trim();
                return produit.getNom().toLowerCase().contains(r)
                        || produit.getCategorie().toLowerCase().contains(r);
            });
            if (produitsFiltre.size() == 1) comboProduits.setValue(produitsFiltre.get(0));
        });

        // ===== COMBO FILTRE TYPE =====
        comboFiltreType.setItems(FXCollections.observableArrayList("Tous", "ENTRÉE", "SORTIE"));
        comboFiltreType.setValue("Tous");

        // ===== FILTERED LIST MOUVEMENTS =====
        mouvementsFiltre = new FilteredList<>(stockManager.getMouvements(), m -> true);

        // Listeners filtres
        comboFiltreType.valueProperty().addListener((obs, o, n) -> appliquerFiltres());
        datePickerFiltre.valueProperty().addListener((obs, o, n) -> appliquerFiltres());
        txtFiltreRecherche.textProperty().addListener((obs, o, n) -> appliquerFiltres());

        // ===== TABLEAU =====
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        // Colonne TYPE avec couleur
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<Mouvement, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else if (type.equals("ENTRÉE")) {
                    setText("⬇️ ENTRÉE");
                    setStyle("-fx-text-fill: #1a7a4a; -fx-font-weight: bold; -fx-background-color: #d5f5e3;");
                } else {
                    setText("⬆️ SORTIE");
                    setStyle("-fx-text-fill: #922b21; -fx-font-weight: bold; -fx-background-color: #fadbd8;");
                }
            }
        });

        // Lignes colorées selon le type
        tableMouvements.setRowFactory(tv -> new TableRow<Mouvement>() {
            @Override
            protected void updateItem(Mouvement mouvement, boolean empty) {
                super.updateItem(mouvement, empty);
                if (empty || mouvement == null) {
                    setStyle("");
                } else if ("ENTRÉE".equals(mouvement.getType())) {
                    setStyle("-fx-background-color: #f9fffe;");
                } else {
                    setStyle("-fx-background-color: #fff9f9;");
                }
            }
        });

        tableMouvements.setItems(mouvementsFiltre);
    }

    // ===== CALCUL NOUVEAU STOCK =====
    private void calculerNouveauStock() {
        Produit produit = comboProduits.getValue();
        String quantiteText = txtQuantite.getText().trim();

        if (produit == null || quantiteText.isEmpty()) {
            lblNouveauStock.setText("—");
            lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            return;
        }

        try {
            double quantiteSortie = Double.parseDouble(quantiteText);
            double stockActuel = produit.getQtyInStock() == null ? 0.0 : produit.getQtyInStock();
            double nouveauStock = stockActuel - quantiteSortie;

            lblNouveauStock.setText(String.format("%.1f", nouveauStock));

            if (nouveauStock < 0) {
                lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            } else if (nouveauStock <= stockActuel * 0.2) {
                // ⚠️ Moins de 20% du stock restant → orange
                lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
            } else {
                lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }

        } catch (NumberFormatException e) {
            lblNouveauStock.setText("—");
        }
    }

    // ===== FILTRES HISTORIQUE =====
    private void appliquerFiltres() {
        String typeFiltre = comboFiltreType.getValue();
        LocalDate dateFiltre = datePickerFiltre.getValue();
        String rechercheFiltre = txtFiltreRecherche.getText().trim().toLowerCase();

        mouvementsFiltre.setPredicate(mouvement -> {

            // Filtre type
            if (typeFiltre != null && !typeFiltre.equals("Tous")) {
                if (!typeFiltre.equals(mouvement.getType())) return false;
            }

            // Filtre date
            if (dateFiltre != null) {
                if (mouvement.getDate() == null) return false;
                LocalDate dateMouvement = mouvement.getDate().toLocalDate();
                if (!dateMouvement.equals(dateFiltre)) return false;
            }

            // Filtre produit / catégorie
            if (!rechercheFiltre.isEmpty()) {
                String nom = mouvement.getProduitNom() == null ? "" : mouvement.getProduitNom().toLowerCase();
                if (!nom.contains(rechercheFiltre)) return false;
            }

            return true;
        });
    }

    // ===== RÉINITIALISER FILTRES =====
    @FXML
    private void reinitialiserFiltres() {
        comboFiltreType.setValue("Tous");
        datePickerFiltre.setValue(null);
        txtFiltreRecherche.clear();
    }

    // ===== AJOUTER SORTIE =====
    @FXML
    private void ajouterSortie() {
        Produit produitSelectionne = comboProduits.getValue();
        String quantiteText = txtQuantite.getText().trim();
        String motif = txtMotif.getText().trim();

        if (produitSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit.");
            return;
        }
        if (quantiteText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une quantité.");
            return;
        }

        try {
            double quantite = Double.parseDouble(quantiteText);

            if (quantite <= 0) {
                showAlert("Erreur", "La quantité doit être positive.");
                return;
            }

            double ancienStock = produitSelectionne.getQtyInStock() == null ? 0.0 : produitSelectionne.getQtyInStock();

            // ✅ Vérification stock suffisant
            if (quantite > ancienStock) {
                showAlert("Erreur", String.format(
                        "⚠️ Stock insuffisant !\n\nStock actuel : %.1f\nQuantité demandée : %.1f",
                        ancienStock, quantite
                ));
                return;
            }

            stockManager.ajouterSortie(produitSelectionne, quantite, motif);

            double nouveauStock = produitSelectionne.getQtyInStock();

            // Reset formulaire
            txtRecherche.clear();
            comboProduits.setValue(null);
            txtQuantite.clear();
            txtMotif.clear();
            lblStockActuel.setText("—");
            lblNouveauStock.setText("—");
            lblNouveauStock.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

            showAlert("Succès", String.format(
                    "✅ Sortie enregistrée !\n\n" +
                            "Produit        : %s\n" +
                            "Ancien stock   : %.1f\n" +
                            "Quantité sortie : -%.1f\n" +
                            "Nouveau stock  : %.1f",
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
