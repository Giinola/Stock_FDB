package pkg.gestion_stock.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import pkg.gestion_stock.service.SessionManager;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnProduits;
    @FXML private Button btnEntree;
    @FXML private Button btnSortie;
    @FXML private Button btnInventaire;
    @FXML private Button btnHistorique;
    @FXML private Button btnUtilisateur;
    @FXML private Button btnLogs;

    @FXML
    public void initialize() {
        System.out.println("✅ MenuController initialisé");

        // RBAC — cacher Utilisateurs et Logs si pas OWNER/MANAGER
        if (!SessionManager.getInstance().canManageUsers()) {
            btnUtilisateur.setVisible(false);
            btnUtilisateur.setManaged(false);
            btnLogs.setVisible(false);
            btnLogs.setManaged(false);
        }

        showDashboard();
    }

    @FXML private void showDashboard()   { loadView("/pkg/gestion_stock/fxml/dashboard.fxml");   setActiveButton(btnDashboard); }
    @FXML private void showProduits()    { loadView("/pkg/gestion_stock/fxml/produits.fxml");     setActiveButton(btnProduits); }
    @FXML private void showEntreeStock() { loadView("/pkg/gestion_stock/fxml/entree_stock.fxml"); setActiveButton(btnEntree); }
    @FXML private void showSortieStock() { loadView("/pkg/gestion_stock/fxml/sortie_stock.fxml"); setActiveButton(btnSortie); }
    @FXML private void showInventaire()  { loadView("/pkg/gestion_stock/fxml/inventaire.fxml");   setActiveButton(btnInventaire); }
    @FXML private void showHistorique()  { loadView("/pkg/gestion_stock/fxml/historique.fxml");   setActiveButton(btnHistorique); }
    @FXML private void showUtilisateur() { loadView("/pkg/gestion_stock/fxml/Utilisateur.fxml");  setActiveButton(btnUtilisateur); }
    @FXML private void showLogs()        { loadView("/pkg/gestion_stock/fxml/logs.fxml");          setActiveButton(btnLogs); }

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
        String activeStyle  = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 12; -fx-alignment: CENTER_LEFT; -fx-background-radius: 5;";

        btnDashboard.setStyle(defaultStyle);
        btnProduits.setStyle(defaultStyle);
        btnEntree.setStyle(defaultStyle);
        btnSortie.setStyle(defaultStyle);
        btnInventaire.setStyle(defaultStyle);
        btnHistorique.setStyle(defaultStyle);
        btnUtilisateur.setStyle(defaultStyle);
        btnLogs.setStyle(defaultStyle);

        activeBtn.setStyle(activeStyle);
    }
    @FXML
    private void logout() {
        try {
            SessionManager.getInstance().logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pkg/gestion_stock/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Stock Manager - Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur logout : " + e.getMessage());
        }
    }
}