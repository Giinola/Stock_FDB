package pkg.gestion_stock.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        loadView("produits.fxml");
    }

    @FXML
    private void goProduits() {
        loadView("produits.fxml");
    }

    @FXML
    private void goEntree() {
        loadView("entree.fxml");
    }

    @FXML
    private void goSortie() {
        loadView("sortie.fxml");
    }

    @FXML
    private void goInventaire() {
        loadView("inventaire.fxml");
    }

    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(
                    getClass().getResource("/pkg/gestion_stock/views/" + fxml)
            );
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
