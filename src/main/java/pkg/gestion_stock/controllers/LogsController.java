package pkg.gestion_stock.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.Database.DatabaseConnection;
import pkg.gestion_stock.models.LogEntry;

import java.sql.*;

public class LogsController {

    @FXML private TableView<LogEntry> tableLogs;
    @FXML private TableColumn<LogEntry, String> colDate;
    @FXML private TableColumn<LogEntry, String> colUsername;
    @FXML private TableColumn<LogEntry, String> colAction;
    @FXML private TableColumn<LogEntry, String> colTable;
    @FXML private TableColumn<LogEntry, String> colDetail;

    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboFiltreAction;
    @FXML private Label lblTotal;

    private ObservableList<LogEntry> tousLesLogs = FXCollections.observableArrayList();
    private FilteredList<LogEntry> logsFiltres;

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableCible"));
        colDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));

        // Couleurs selon action
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String action, boolean empty) {
                super.updateItem(action, empty);
                if (empty || action == null) { setText(null); setStyle(""); return; }
                setText(action);
                switch (action) {
                    case "AJOUT"       -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "MODIF"       -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    case "SUPPRESSION" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default            -> setStyle("-fx-text-fill: #8e44ad; -fx-font-weight: bold;");
                }
            }
        });

        comboFiltreAction.setItems(FXCollections.observableArrayList("Tous", "AJOUT", "MODIF", "SUPPRESSION"));
        comboFiltreAction.setValue("Tous");

        logsFiltres = new FilteredList<>(tousLesLogs, p -> true);
        tableLogs.setItems(logsFiltres);

        txtRecherche.textProperty().addListener((obs, o, n) -> appliquerFiltres());
        comboFiltreAction.valueProperty().addListener((obs, o, n) -> appliquerFiltres());

        chargerLogs();
    }

    private void chargerLogs() {
        tousLesLogs.clear();
        String sql = "SELECT username, action, table_cible, detail, date_action FROM audit_log ORDER BY date_action DESC";

        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("date_action");
                String date = ts != null ? ts.toLocalDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
                tousLesLogs.add(new LogEntry(
                        date,
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getString("table_cible"),
                        rs.getString("detail")
                ));
            }
            lblTotal.setText("Total : " + tousLesLogs.size() + " actions");

        } catch (SQLException e) {
            System.out.println("Erreur chargement logs : " + e.getMessage());
        }
    }

    private void appliquerFiltres() {
        String recherche = txtRecherche.getText().toLowerCase().trim();
        String action = comboFiltreAction.getValue();

        logsFiltres.setPredicate(log -> {
            boolean matchAction = "Tous".equals(action) || action.equals(log.getAction());
            boolean matchRecherche = recherche.isEmpty()
                    || (log.getUsername() != null && log.getUsername().toLowerCase().contains(recherche))
                    || (log.getDetail() != null && log.getDetail().toLowerCase().contains(recherche))
                    || (log.getTableCible() != null && log.getTableCible().toLowerCase().contains(recherche));
            return matchAction && matchRecherche;
        });

        lblTotal.setText("Total : " + logsFiltres.size() + " actions");
    }

    @FXML
    private void actualiser() {
        chargerLogs();
        appliquerFiltres();
    }
}