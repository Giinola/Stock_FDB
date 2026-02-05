package pkg.gestion_stock.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;

public class MenuController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnProduits;
    @FXML private Button btnEntree;
    @FXML private Button btnSortie;
    @FXML private Button btnInventaire;

    @FXML
    public void initialize() {
        System.out.println("✅ MenuController initialisé");
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/pkg/gestion_stock/fxml/dashboard.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void showProduits() {
        loadView("/pkg/gestion_stock/fxml/produits.fxml");
        setActiveButton(btnProduits);
    }

    @FXML
    private void showEntreeStock() {
        loadView("/pkg/gestion_stock/fxml/entree-stock.fxml");
        setActiveButton(btnEntree);
    }

    @FXML
    private void showSortieStock() {
        loadView("/pkg/gestion_stock/fxml/sortie-stock.fxml");
        setActiveButton(btnSortie);
    }

    @FXML
    private void showInventaire() {
        loadView("/pkg/gestion_stock/fxml/inventaire.fxml");
        setActiveButton(btnInventaire);
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("🔄 Chargement de : " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("✅ Vue chargée avec succès");
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeBtn) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14; -fx-padding: 12; -fx-alignment: CENTER_LEFT; -fx-background-radius: 5;";
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 12; -fx-alignment: CENTER_LEFT; -fx-background-radius: 5;";

        btnDashboard.setStyle(defaultStyle);
        btnProduits.setStyle(defaultStyle);
        btnEntree.setStyle(defaultStyle);
        btnSortie.setStyle(defaultStyle);
        btnInventaire.setStyle(defaultStyle);

        activeBtn.setStyle(activeStyle);
    }
}