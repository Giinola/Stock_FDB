package pkg.gestion_stock.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pkg.gestion_stock.service.AuthenticationService;

public class LogiinController {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    private AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        System.out.println("LoginController initialise");

        if (lblError != null) {
            lblError.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        if (authService.login(username, password)) {
            System.out.println("Connexion reussie pour : " + username);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pkg/gestion_stock/fxml/menu.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, 1200, 700);
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Stock Manager - " + username);
                stage.show();

            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du menu");
                e.printStackTrace();
                showError("Erreur de chargement de l'application");
            }

        } else {
            showError("Nom d'utilisateur ou mot de passe incorrect");
            txtPassword.clear();
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12;");
            lblError.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}
