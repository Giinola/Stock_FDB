package pkg.gestion_stock.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.dao.ProduitDAO;
import pkg.gestion_stock.models.Produit;
import pkg.gestion_stock.models.StockManager;
import pkg.gestion_stock.service.AuditService;
import pkg.gestion_stock.service.SessionManager;

public class ProduitController {

    // ===== TABLE =====
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colQuantite;

    // ===== FORMULAIRE =====
    @FXML private TextField txtCategorie;
    @FXML private TextField txtNom;
    @FXML private TextField txtQuantite;

    // ===== BOUTONS =====
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    // ===== STATS =====
    @FXML private Label lblTotalProduits;
    @FXML private Label lblTotalCategories;

    private final ObservableList<Produit> data = StockManager.getInstance().getProduits();
    private final ProduitDAO dao = new ProduitDAO();

    @FXML
    public void initialize() {
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("qtyInStock"));
        colQuantite.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "—" : String.format("%.1f", item));
            }
        });

        tableProduits.setRowFactory(tv -> new TableRow<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else if (item.getNom() == null) setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");
                else setStyle("");
            }
        });

        tableProduits.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null && selected.getNom() != null) remplirFormulaire(selected);
        });

        tableProduits.setItems(data);
        tableProduits.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        majStats();

        // ===== RBAC =====
        if (!SessionManager.getInstance().canManageUsers()) {
            btnAjouter.setDisable(true);
            btnModifier.setDisable(true);
            btnSupprimer.setDisable(true);
            txtCategorie.setEditable(false);
            txtNom.setEditable(false);
            txtQuantite.setEditable(false);
        }
    }

    // ===== AJOUTER =====
    @FXML
    private void ajouter() {
        if (!validerFormulaire()) return;

        double qte = txtQuantite.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtQuantite.getText().trim());
        Produit p = new Produit(txtCategorie.getText().trim(), txtNom.getText().trim(), qte);

        dao.inserer(p);

        // Insérer à la bonne position dans la catégorie
        int insertIndex = -1;
        for (int i = data.size() - 1; i >= 0; i--) {
            if (txtCategorie.getText().trim().equals(data.get(i).getCategorie())) {
                insertIndex = i + 1;
                break;
            }
        }
        if (insertIndex == -1) data.add(p);
        else data.add(insertIndex, p);

        AuditService.getInstance().log("AJOUT", "produit", p.getCategorie() + " - " + p.getNom());
        majStats();
        viderFormulaire();
        showInfo("Produit ajouté.");
    }

    // ===== MODIFIER =====
    @FXML
    private void modifier() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getNom() == null) { showErreur("Sélectionnez un produit."); return; }
        if (txtQuantite.getText().trim().isEmpty()) { showErreur("Entrez une quantité."); return; }

        try {
            double nouvelleQte = Double.parseDouble(txtQuantite.getText().trim());
            dao.mettreAJourQuantite(selected.getCategorie(), selected.getNom(), nouvelleQte);
            selected.setQtyInStock(nouvelleQte);
            tableProduits.refresh();

            AuditService.getInstance().log("MODIF", "produit", selected.getCategorie() + " - " + selected.getNom());
            majStats();
            viderFormulaire();
            showInfo("Produit modifié.");
        } catch (NumberFormatException e) {
            showErreur("Quantité invalide.");
        }
    }

    // ===== SUPPRIMER =====
    @FXML
    private void supprimer() {
        Produit selected = tableProduits.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getNom() == null) { showErreur("Sélectionnez un produit."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + selected.getNom() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dao.supprimer(selected.getCategorie(), selected.getNom());
                data.remove(selected);

                AuditService.getInstance().log("SUPPRESSION", "produit", selected.getCategorie() + " - " + selected.getNom());
                majStats();
                viderFormulaire();
                showInfo("Produit supprimé.");
            }
        });
    }

    // ===== ACTUALISER =====
    @FXML
    private void actualiser() {
        data.setAll(dao.findAll());
        majStats();
    }

    // ===== STATS =====
    private void majStats() {
        long total = data.stream().filter(p -> p.getNom() != null).count();
        long categories = data.stream().filter(p -> p.getNom() != null)
                .map(Produit::getCategorie).distinct().count();
        lblTotalProduits.setText(String.valueOf(total));
        lblTotalCategories.setText(String.valueOf(categories));
    }

    // ===== HELPERS =====
    private void remplirFormulaire(Produit p) {
        txtCategorie.setText(p.getCategorie() != null ? p.getCategorie() : "");
        txtNom.setText(p.getNom() != null ? p.getNom() : "");
        txtQuantite.setText(p.getQtyInStock() != null ? String.format("%.1f", p.getQtyInStock()) : "");
    }

    @FXML
    public void viderFormulaire() {
        txtCategorie.clear();
        txtNom.clear();
        txtQuantite.clear();
        tableProduits.getSelectionModel().clearSelection();
    }

    private boolean validerFormulaire() {
        if (txtCategorie.getText().trim().isEmpty() || txtNom.getText().trim().isEmpty()) {
            showErreur("Catégorie et nom obligatoires.");
            return false;
        }
        return true;
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void showErreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}















