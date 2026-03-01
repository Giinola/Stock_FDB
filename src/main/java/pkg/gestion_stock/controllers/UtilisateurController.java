package pkg.gestion_stock.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.dao.UtilisateurDAO;
import pkg.gestion_stock.models.Utilisateur;
import pkg.gestion_stock.service.AuditService;
import pkg.gestion_stock.service.SessionManager;
import java.util.List;

public class UtilisateurController {

    // ===== TABLE =====
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colPrenom;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colTelephone;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, Boolean> colActif;

    // ===== FORMULAIRE =====
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private ComboBox<String> comboRole;
    @FXML private CheckBox chkActif;

    // ===== BOUTONS CRUD =====
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    @FXML private Label lblTotalUsers;
    @FXML private Label lblUsersActifs;
    @FXML private Label lblUsersVerrouilles;
    @FXML private Label lblUsersAdmin;

    private UtilisateurDAO dao = new UtilisateurDAO();
    private ObservableList<Utilisateur> liste = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Colonnes
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        // Rôles disponibles
        comboRole.setItems(FXCollections.observableArrayList("OWNER", "MANAGER", "EMPLOYE"));
        comboRole.setValue("EMPLOYE");

        // Sélection dans la table → remplir le formulaire
        tableUtilisateurs.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) remplirFormulaire(selected);
        });

        // ===== RBAC : désactiver CRUD si pas autorisé =====
        if (!SessionManager.getInstance().canManageUsers()) {
            btnAjouter.setDisable(true);
            btnModifier.setDisable(true);
            btnSupprimer.setDisable(true);
            txtUsername.setEditable(false);
            txtPassword.setEditable(false);
            txtNom.setEditable(false);
            txtPrenom.setEditable(false);
            txtEmail.setEditable(false);
            txtTelephone.setEditable(false);
            comboRole.setDisable(true);
        }

        chargerDonnees();
    }

    // ===== CHARGER =====
    private void chargerDonnees() {
        liste.setAll(dao.findAll());
        tableUtilisateurs.setItems(liste);
        majStats();
    }

    // ===== AJOUTER =====
    @FXML
    private void ajouter() {
        if (!validerFormulaire()) return;

        Utilisateur u = new Utilisateur(txtUsername.getText().trim(), txtPassword.getText().trim());
        u.setNom(txtNom.getText().trim());
        u.setPrenom(txtPrenom.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setTelephone(txtTelephone.getText().trim());

        String role = comboRole.getValue();

        if (dao.insert(u, role)) {
            AuditService.getInstance().log("AJOUT", "utilisateur", "User: " + u.getUsername() + " | Role: " + role);
            chargerDonnees();
            viderFormulaire();
            showInfo("Utilisateur ajouté avec succès.");
        } else {
            showErreur("Erreur lors de l'ajout.");
        }
    }

    // ===== MODIFIER =====
    @FXML
    private void modifier() {
        Utilisateur selected = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (selected == null) { showErreur("Sélectionnez un utilisateur."); return; }

        selected.setNom(txtNom.getText().trim());
        selected.setPrenom(txtPrenom.getText().trim());
        selected.setEmail(txtEmail.getText().trim());
        selected.setTelephone(txtTelephone.getText().trim());
        selected.setActif(chkActif.isSelected());

        if (dao.update(selected)) {
            AuditService.getInstance().log("MODIF", "utilisateur", "User: " + selected.getUsername());
            chargerDonnees();
            viderFormulaire();
            showInfo("Utilisateur modifié avec succès.");
        } else {
            showErreur("Erreur lors de la modification.");
        }
    }

    // ===== SUPPRIMER =====
    @FXML
    private void supprimer() {
        Utilisateur selected = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (selected == null) { showErreur("Sélectionnez un utilisateur."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + selected.getUsername() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dao.delete(selected.getId())) {
                    AuditService.getInstance().log("SUPPRESSION", "utilisateur", "User: " + selected.getUsername());
                    chargerDonnees();
                    viderFormulaire();
                    showInfo("Utilisateur supprimé.");
                } else {
                    showErreur("Erreur lors de la suppression.");
                }
            }
        });
    }

     @FXML
    private void remplirFormulaire(Utilisateur u) {
        txtUsername.setText(u.getUsername());
        txtPassword.setText("");
        txtNom.setText(u.getNom() != null ? u.getNom() : "");
        txtPrenom.setText(u.getPrenom() != null ? u.getPrenom() : "");
        txtEmail.setText(u.getEmail() != null ? u.getEmail() : "");
        txtTelephone.setText(u.getTelephone() != null ? u.getTelephone() : "");
        comboRole.setValue(u.getRole() != null ? u.getRole() : "EMPLOYE");
        chkActif.setSelected(u.isActif());
    }
    @FXML
    private void viderFormulaire() {
        txtUsername.clear(); txtPassword.clear(); txtNom.clear();
        txtPrenom.clear(); txtEmail.clear(); txtTelephone.clear();
        comboRole.setValue("EMPLOYE");
        chkActif.setSelected(true);
        tableUtilisateurs.getSelectionModel().clearSelection();
    }
    @FXML
    private boolean validerFormulaire() {
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty()) {
            showErreur("Username et mot de passe obligatoires.");
            return false;
        }
        return true;
    }
    @FXML
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    @FXML
    private void showErreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void majStats() {
        List<Utilisateur> tous = dao.findAll();
        lblTotalUsers.setText(String.valueOf(tous.size()));
        lblUsersActifs.setText(String.valueOf(tous.stream().filter(Utilisateur::isActif).count()));
        lblUsersVerrouilles.setText(String.valueOf(tous.stream().filter(Utilisateur::isCompteVerrouille).count()));
        lblUsersAdmin.setText(String.valueOf(tous.stream().filter(u -> "OWNER".equals(u.getRole()) || "MANAGER".equals(u.getRole())).count()));
    }
}
