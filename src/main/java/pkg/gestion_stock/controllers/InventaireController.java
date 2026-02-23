package pkg.gestion_stock.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import pkg.gestion_stock.models.Mouvement;
import pkg.gestion_stock.models.Produit;
import pkg.gestion_stock.models.StockManager;

import java.util.*;
import java.util.stream.Collectors;

public class InventaireController {

    // ===== STATS CARDS =====
    @FXML private Label lblTotalProduits;
    @FXML private Label lblProduitsOK;
    @FXML private Label lblProduitsAlerte;
    @FXML private Label lblProduitsRupture;

    // ===== SEUIL =====
    @FXML private TextField txtSeuil;
    @FXML private Label lblSeuilActuel;

    // ===== TABLEAU INVENTAIRE =====
    @FXML private TableView<LigneProduit> tableInventaire;
    @FXML private TableColumn<LigneProduit, String> colCategorie;
    @FXML private TableColumn<LigneProduit, String> colProduit;
    @FXML private TableColumn<LigneProduit, Double> colStockActuel;
    @FXML private TableColumn<LigneProduit, Double> colTotalEntrees;
    @FXML private TableColumn<LigneProduit, Double> colTotalSorties;
    @FXML private TableColumn<LigneProduit, String> colStatut;

    // ===== TABLEAU ALERTES =====
    @FXML private TableView<LigneProduit> tableAlertes;
    @FXML private TableColumn<LigneProduit, String> colAlertCategorie;
    @FXML private TableColumn<LigneProduit, String> colAlertProduit;
    @FXML private TableColumn<LigneProduit, Double> colAlertStock;
    @FXML private TableColumn<LigneProduit, String> colAlertStatut;

    // ===== FILTRE =====
    @FXML private ComboBox<String> comboFiltreCategorie;

    private StockManager stockManager = StockManager.getInstance();
    private double seuilMinimal = 10.0;
    private ObservableList<LigneProduit> toutesLesLignes = FXCollections.observableArrayList();

    // =========================================================
    // MODÈLE INTERNE
    // =========================================================
    public static class LigneProduit {
        private final SimpleStringProperty categorie;
        private final SimpleStringProperty produit;
        private final SimpleDoubleProperty stockActuel;
        private final SimpleDoubleProperty totalEntrees;
        private final SimpleDoubleProperty totalSorties;
        private final SimpleStringProperty statut;

        public LigneProduit(String categorie, String produit,
                            double stockActuel, double totalEntrees,
                            double totalSorties, String statut) {
            this.categorie    = new SimpleStringProperty(categorie);
            this.produit      = new SimpleStringProperty(produit);
            this.stockActuel  = new SimpleDoubleProperty(stockActuel);
            this.totalEntrees = new SimpleDoubleProperty(totalEntrees);
            this.totalSorties = new SimpleDoubleProperty(totalSorties);
            this.statut       = new SimpleStringProperty(statut);
        }

        public String getCategorie()    { return categorie.get(); }
        public String getProduit()      { return produit.get(); }
        public double getStockActuel()  { return stockActuel.get(); }
        public double getTotalEntrees() { return totalEntrees.get(); }
        public double getTotalSorties() { return totalSorties.get(); }
        public String getStatut()       { return statut.get(); }
    }

    // =========================================================
    // INITIALIZE
    // =========================================================
    @FXML
    public void initialize() {
        System.out.println("✅ InventaireController initialisé");

        txtSeuil.setText(String.valueOf((int) seuilMinimal));
        lblSeuilActuel.setText("Seuil actuel : " + (int) seuilMinimal);

        configurerTableInventaire();
        configurerTableAlertes();
        chargerDonnees();
    }

    // =========================================================
    // CONFIGURATION TABLEAUX
    // =========================================================
    private void configurerTableInventaire() {
        colCategorie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategorie()));
        colProduit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduit()));
        colStockActuel.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getStockActuel()).asObject());
        colTotalEntrees.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalEntrees()).asObject());
        colTotalSorties.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalSorties()).asObject());
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut()));

        // Colonne statut colorée
        colStatut.setCellFactory(col -> new TableCell<LigneProduit, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null); setStyle("");
                } else if (statut.equals("✅ OK")) {
                    setText(statut);
                    setStyle("-fx-text-fill: #1a7a4a; -fx-font-weight: bold; -fx-background-color: #d5f5e3;");
                } else if (statut.equals("⚠️ Bas")) {
                    setText(statut);
                    setStyle("-fx-text-fill: #9a5e00; -fx-font-weight: bold; -fx-background-color: #fef9e7;");
                } else {
                    setText(statut);
                    setStyle("-fx-text-fill: #922b21; -fx-font-weight: bold; -fx-background-color: #fadbd8;");
                }
            }
        });

        // Lignes colorées selon statut
        tableInventaire.setRowFactory(tv -> new TableRow<LigneProduit>() {
            @Override
            protected void updateItem(LigneProduit ligne, boolean empty) {
                super.updateItem(ligne, empty);
                if (empty || ligne == null) {
                    setStyle("");
                } else if (ligne.getStatut().equals("🔴 Rupture")) {
                    setStyle("-fx-background-color: #fff2f2;");
                } else if (ligne.getStatut().equals("⚠️ Bas")) {
                    setStyle("-fx-background-color: #fffdf0;");
                } else {
                    setStyle("");
                }
            }
        });

        // Colonne stock actuel colorée
        colStockActuel.setCellFactory(col -> new TableCell<LigneProduit, Double>() {
            @Override
            protected void updateItem(Double stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null); setStyle("");
                } else {
                    setText(String.format("%.1f", stock));
                    if (stock <= 0) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (stock <= seuilMinimal) {
                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurerTableAlertes() {
        colAlertCategorie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategorie()));
        colAlertProduit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduit()));
        colAlertStock.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getStockActuel()).asObject());
        colAlertStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut()));

        colAlertStock.setCellFactory(col -> new TableCell<LigneProduit, Double>() {
            @Override
            protected void updateItem(Double stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null); setStyle("");
                } else {
                    setText(String.format("%.1f", stock));
                    if (stock <= 0) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13;");
                    } else {
                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-size: 13;");
                    }
                }
            }
        });

        colAlertStatut.setCellFactory(col -> new TableCell<LigneProduit, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null); setStyle("");
                } else if (statut.equals("🔴 Rupture")) {
                    setText(statut);
                    setStyle("-fx-text-fill: #922b21; -fx-font-weight: bold; -fx-background-color: #fadbd8;");
                } else {
                    setText(statut);
                    setStyle("-fx-text-fill: #9a5e00; -fx-font-weight: bold; -fx-background-color: #fef9e7;");
                }
            }
        });

        tableAlertes.setRowFactory(tv -> new TableRow<LigneProduit>() {
            @Override
            protected void updateItem(LigneProduit ligne, boolean empty) {
                super.updateItem(ligne, empty);
                if (empty || ligne == null) setStyle("");
                else if (ligne.getStatut().equals("🔴 Rupture")) setStyle("-fx-background-color: #fff2f2;");
                else setStyle("-fx-background-color: #fffdf0;");
            }
        });
    }

    // =========================================================
    // CHARGER LES DONNÉES
    // =========================================================
    private void chargerDonnees() {
        toutesLesLignes.clear();

        List<Produit> produits = stockManager.getProduitsUniquement();
        List<Mouvement> mouvements = stockManager.getMouvements();

        // Calculer totaux entrées/sorties par produit
        Map<String, Double> totalEntreesMap = new HashMap<>();
        Map<String, Double> totalSortiesMap = new HashMap<>();

        for (Mouvement m : mouvements) {
            if (m.getProduitNom() == null) continue;
            String nom = m.getProduitNom();
            if ("ENTRÉE".equals(m.getType())) {
                totalEntreesMap.merge(nom, m.getQuantite() != null ? m.getQuantite() : 0.0, Double::sum);
            } else if ("SORTIE".equals(m.getType())) {
                totalSortiesMap.merge(nom, m.getQuantite() != null ? m.getQuantite() : 0.0, Double::sum);
            }
        }

        // Trier par catégorie
        produits.sort(Comparator.comparing(Produit::getCategorie, Comparator.nullsLast(String::compareTo)));

        for (Produit p : produits) {
            double stock    = p.getQtyInStock() != null ? p.getQtyInStock() : 0.0;
            double entrees  = totalEntreesMap.getOrDefault(p.getNom(), 0.0);
            double sorties  = totalSortiesMap.getOrDefault(p.getNom(), 0.0);

            String statut;
            if (stock <= 0) {
                statut = "🔴 Rupture";
            } else if (stock <= seuilMinimal) {
                statut = "⚠️ Bas";
            } else {
                statut = "✅ OK";
            }

            toutesLesLignes.add(new LigneProduit(
                    p.getCategorie() != null ? p.getCategorie() : "",
                    p.getNom() != null ? p.getNom() : "",
                    stock, entrees, sorties, statut
            ));
        }

        // Charger dans les tableaux
        tableInventaire.setItems(toutesLesLignes);
        majTableAlertes();
        majCardsStats();
        majFiltreCategorie();
    }

    // =========================================================
    // TABLE ALERTES
    // =========================================================
    private void majTableAlertes() {
        ObservableList<LigneProduit> alertes = FXCollections.observableArrayList(
                toutesLesLignes.stream()
                        .filter(l -> l.getStatut().equals("🔴 Rupture") || l.getStatut().equals("⚠️ Bas"))
                        .sorted(Comparator.comparingDouble(LigneProduit::getStockActuel))
                        .collect(Collectors.toList())
        );
        tableAlertes.setItems(alertes);
    }

    // =========================================================
    // CARDS STATISTIQUES
    // =========================================================
    private void majCardsStats() {
        long total    = toutesLesLignes.size();
        long ok       = toutesLesLignes.stream().filter(l -> l.getStatut().equals("✅ OK")).count();
        long bas      = toutesLesLignes.stream().filter(l -> l.getStatut().equals("⚠️ Bas")).count();
        long rupture  = toutesLesLignes.stream().filter(l -> l.getStatut().equals("🔴 Rupture")).count();

        lblTotalProduits.setText(String.valueOf(total));
        lblProduitsOK.setText(String.valueOf(ok));
        lblProduitsAlerte.setText(String.valueOf(bas));
        lblProduitsRupture.setText(String.valueOf(rupture));
    }

    // =========================================================
    // FILTRE PAR CATÉGORIE
    // =========================================================
    private void majFiltreCategorie() {
        List<String> categories = toutesLesLignes.stream()
                .map(LigneProduit::getCategorie)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        ObservableList<String> items = FXCollections.observableArrayList("Toutes");
        items.addAll(categories);
        comboFiltreCategorie.setItems(items);
        comboFiltreCategorie.setValue("Toutes");
    }

    @FXML
    private void filtrerParCategorie() {
        String selection = comboFiltreCategorie.getValue();
        if (selection == null || selection.equals("Toutes")) {
            tableInventaire.setItems(toutesLesLignes);
        } else {
            ObservableList<LigneProduit> filtrees = FXCollections.observableArrayList(
                    toutesLesLignes.stream()
                            .filter(l -> l.getCategorie().equals(selection))
                            .collect(Collectors.toList())
            );
            tableInventaire.setItems(filtrees);
        }
    }

    // =========================================================
    // CHANGER LE SEUIL
    // =========================================================
    @FXML
    private void appliquerSeuil() {
        String texte = txtSeuil.getText().trim();
        try {
            double nouveauSeuil = Double.parseDouble(texte);
            if (nouveauSeuil < 0) {
                showAlert("Erreur", "Le seuil doit être positif.");
                return;
            }
            seuilMinimal = nouveauSeuil;
            lblSeuilActuel.setText("Seuil actuel : " + (int) seuilMinimal);
            chargerDonnees(); // Recalcule tout avec le nouveau seuil
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide.");
        }
    }

    // =========================================================
    // RAFRAÎCHIR
    // =========================================================
    @FXML
    private void rafraichir() {
        chargerDonnees();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}