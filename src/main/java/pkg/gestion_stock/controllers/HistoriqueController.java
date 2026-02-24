package pkg.gestion_stock.controllers;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pkg.gestion_stock.models.Mouvement;
import pkg.gestion_stock.models.StockManager;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueController {

    // ===== STATS =====
    @FXML private Label lblTotalEntrees;
    @FXML private Label lblTotalSorties;
    @FXML private Label lblTotalMouvements;
    @FXML private Label lblSelectionnes;

    // ===== FILTRES =====
    @FXML private ComboBox<String> comboFiltreType;
    @FXML private DatePicker datePickerFiltre;
    @FXML private TextField txtFiltreRecherche;

    // ===== SELECTION =====
    @FXML private CheckBox chkToutSelectionner;
    @FXML private HBox bandeauSelection;
    @FXML private Label lblInfoSelection;

    // ===== TABLEAU =====
    @FXML private TableView<Mouvement> tableMouvements;
    @FXML private TableColumn<Mouvement, Boolean> colCheck;
    @FXML private TableColumn<Mouvement, String>  colDate;
    @FXML private TableColumn<Mouvement, String>  colType;
    @FXML private TableColumn<Mouvement, String>  colProduit;
    @FXML private TableColumn<Mouvement, Double>  colQuantite;
    @FXML private TableColumn<Mouvement, String>  colMotif;

    private StockManager stockManager = StockManager.getInstance();
    private FilteredList<Mouvement> mouvementsFiltre;

    // Liste des mouvements cochés
    private final ObservableList<Mouvement> selectionnes = FXCollections.observableArrayList();

    // =========================================================
    @FXML
    public void initialize() {
        System.out.println("✅ HistoriqueController initialisé");

        comboFiltreType.setItems(FXCollections.observableArrayList("Tous", "ENTRÉE", "SORTIE"));
        comboFiltreType.setValue("Tous");

        mouvementsFiltre = new FilteredList<>(stockManager.getMouvements(), m -> true);

        mettreAJourStatistiques();

        // Listeners filtres
        comboFiltreType.valueProperty().addListener((obs, o, n) -> { appliquerFiltres(); mettreAJourStatistiques(); });
        datePickerFiltre.valueProperty().addListener((obs, o, n) -> { appliquerFiltres(); mettreAJourStatistiques(); });
        txtFiltreRecherche.textProperty().addListener((obs, o, n) -> { appliquerFiltres(); mettreAJourStatistiques(); });

        configurerTableau();
        tableMouvements.setItems(mouvementsFiltre);
    }

    // =========================================================
    // CONFIGURATION TABLEAU
    // =========================================================
    private void configurerTableau() {

        // ---- Colonne checkbox ----
        colCheck.setCellFactory(col -> new TableCell<Mouvement, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Mouvement m = getTableView().getItems().get(getIndex());
                    if (checkBox.isSelected()) {
                        if (!selectionnes.contains(m)) selectionnes.add(m);
                    } else {
                        selectionnes.remove(m);
                        chkToutSelectionner.setSelected(false);
                    }
                    majBandeauSelection();
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Mouvement m = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(selectionnes.contains(m));
                    setGraphic(checkBox);
                }
            }
        });
        colCheck.setSortable(false);

        // ---- Autres colonnes ----
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        // Colonne quantité avec signe + / -
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colQuantite.setCellFactory(col -> new TableCell<Mouvement, Double>() {
            @Override
            protected void updateItem(Double qte, boolean empty) {
                super.updateItem(qte, empty);
                if (empty || qte == null) { setText(null); setStyle(""); return; }
                Mouvement m = getTableView().getItems().get(getIndex());
                if ("ENTRÉE".equals(m.getType())) {
                    setText(String.format("+ %.1f", qte));
                    setStyle("-fx-text-fill: #1a7a4a; -fx-font-weight: bold;");
                } else {
                    setText(String.format("- %.1f", qte));
                    setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                }
            }
        });

        // Colonne type colorée
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<Mouvement, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) { setText(null); setStyle(""); return; }
                if (type.equals("ENTRÉE")) {
                    setText("⬇️ ENTRÉE");
                    setStyle("-fx-text-fill: #1e8449; -fx-font-weight: bold; -fx-background-color: #d5f4e6;");
                } else {
                    setText("⬆️ SORTIE");
                    setStyle("-fx-text-fill: #922b21; -fx-font-weight: bold; -fx-background-color: #fadbd8;");
                }
            }
        });

        // Lignes colorées
        tableMouvements.setRowFactory(tv -> new TableRow<Mouvement>() {
            @Override
            protected void updateItem(Mouvement m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) setStyle("");
                else if ("ENTRÉE".equals(m.getType())) setStyle("-fx-background-color: #f9fffe;");
                else setStyle("-fx-background-color: #fff9f9;");
            }
        });
    }

    // =========================================================
    // SÉLECTION
    // =========================================================
    @FXML
    private void toutSelectionner() {
        selectionnes.clear();
        if (chkToutSelectionner.isSelected()) {
            selectionnes.addAll(mouvementsFiltre);
        }
        tableMouvements.refresh();
        majBandeauSelection();
    }

    @FXML
    private void deselectionnerTout() {
        selectionnes.clear();
        chkToutSelectionner.setSelected(false);
        tableMouvements.refresh();
        majBandeauSelection();
    }

    private void majBandeauSelection() {
        int nb = selectionnes.size();
        lblSelectionnes.setText(String.valueOf(nb));

        if (nb > 0) {
            bandeauSelection.setVisible(true);
            bandeauSelection.setManaged(true);
            lblInfoSelection.setText(nb + " ligne(s) sélectionnée(s) — l'export portera sur la sélection");
        } else {
            bandeauSelection.setVisible(false);
            bandeauSelection.setManaged(false);
        }
    }

    // Retourne la liste à exporter : sélection si cochés, sinon tout ce qui est affiché
    private List<Mouvement> getListeAExporter() {
        if (!selectionnes.isEmpty()) return new ArrayList<>(selectionnes);
        return new ArrayList<>(mouvementsFiltre);
    }

    // =========================================================
    // STATISTIQUES
    // =========================================================
    private void mettreAJourStatistiques() {
        long entrees = mouvementsFiltre.stream().filter(m -> "ENTRÉE".equals(m.getType())).count();
        long sorties = mouvementsFiltre.stream().filter(m -> "SORTIE".equals(m.getType())).count();
        lblTotalEntrees.setText(String.valueOf(entrees));
        lblTotalSorties.setText(String.valueOf(sorties));
        lblTotalMouvements.setText(String.valueOf(mouvementsFiltre.size()));
    }

    // =========================================================
    // FILTRES
    // =========================================================
    private void appliquerFiltres() {
        String typeFiltre      = comboFiltreType.getValue();
        LocalDate dateFiltre   = datePickerFiltre.getValue();
        String rechercheFiltre = txtFiltreRecherche.getText().trim().toLowerCase();

        mouvementsFiltre.setPredicate(mouvement -> {
            if (typeFiltre != null && !typeFiltre.equals("Tous")) {
                if (!typeFiltre.equals(mouvement.getType())) return false;
            }
            if (dateFiltre != null) {
                if (mouvement.getDate() == null) return false;
                if (!mouvement.getDate().toLocalDate().equals(dateFiltre)) return false;
            }
            if (!rechercheFiltre.isEmpty()) {
                String nom = mouvement.getProduitNom() == null ? "" : mouvement.getProduitNom().toLowerCase();
                if (!nom.contains(rechercheFiltre)) return false;
            }
            return true;
        });

        // Nettoyer la sélection si des lignes ne sont plus visibles
        selectionnes.retainAll(mouvementsFiltre);
        tableMouvements.refresh();
        majBandeauSelection();
    }

    @FXML
    private void reinitialiserFiltres() {
        comboFiltreType.setValue("Tous");
        datePickerFiltre.setValue(null);
        txtFiltreRecherche.clear();
    }

    // =========================================================
    // EXPORT EXCEL (Apache POI)
    // =========================================================
    @FXML
    private void exporterExcel() {
        List<Mouvement> liste = getListeAExporter();
        if (liste.isEmpty()) { showAlert("Attention", "⚠️ Aucun mouvement à exporter."); return; }

        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le fichier Excel");
        fc.setInitialFileName("historique_stock.xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Excel", "*.xlsx"));
        File fichier = fc.showSaveDialog(tableMouvements.getScene().getWindow());
        if (fichier == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique");

            // Style en-tête
            CellStyle styleEntete = workbook.createCellStyle();
            styleEntete.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fontEntete = workbook.createFont();
            fontEntete.setColor(IndexedColors.WHITE.getIndex());
            fontEntete.setBold(true);
            styleEntete.setFont(fontEntete);
            styleEntete.setAlignment(HorizontalAlignment.CENTER);

            // Style ENTRÉE
            CellStyle styleEntree = workbook.createCellStyle();
            styleEntree.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            styleEntree.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Style SORTIE
            CellStyle styleSortie = workbook.createCellStyle();
            styleSortie.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            styleSortie.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // En-têtes
            String[] entetes = {"Date", "Type", "Produit", "Quantité", "Motif"};
            Row rowEntete = sheet.createRow(0);
            for (int i = 0; i < entetes.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = rowEntete.createCell(i);
                cell.setCellValue(entetes[i]);
                cell.setCellStyle(styleEntete);
            }

            // Données
            for (int i = 0; i < liste.size(); i++) {
                Mouvement m = liste.get(i);
                Row row = sheet.createRow(i + 1);
                CellStyle styleRow = "ENTRÉE".equals(m.getType()) ? styleEntree : styleSortie;

                String[] valeurs = {
                        m.getDateFormatee() != null ? m.getDateFormatee() : "",
                        m.getType() != null ? m.getType() : "",
                        m.getProduitNom() != null ? m.getProduitNom() : "",
                        m.getQuantite() != null ? String.format("%.1f", m.getQuantite()) : "",
                        m.getMotif() != null ? m.getMotif() : ""
                };

                for (int j = 0; j < valeurs.length; j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
                    cell.setCellValue(valeurs[j]);
                    cell.setCellStyle(styleRow);
                }
            }

            // Ajuster largeur colonnes
            for (int i = 0; i < entetes.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                workbook.write(fos);
            }

            showSuccess("✅ Excel exporté !\n\n📁 " + fichier.getAbsolutePath());

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'export Excel : " + e.getMessage());
        }
    }

    // =========================================================
    // EXPORT PDF (iTextPDF)
    // =========================================================
    @FXML
    private void exporterPDF() {
        List<Mouvement> liste = getListeAExporter();
        if (liste.isEmpty()) { showAlert("Attention", "⚠️ Aucun mouvement à exporter."); return; }

        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le fichier PDF");
        fc.setInitialFileName("historique_stock.pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File fichier = fc.showSaveDialog(tableMouvements.getScene().getWindow());
        if (fichier == null) return;

        try {
            PdfWriter writer   = new PdfWriter(fichier.getAbsolutePath());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document  = new Document(pdfDoc);

            // Titre
            document.add(new Paragraph("Historique des mouvements de stock")
                    .setFontSize(18).setBold()
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(5));

            // Date de génération
            String dateGen = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("Généré le : " + dateGen + "   —   " + liste.size() + " mouvement(s)")
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY).setMarginBottom(15));

            // Tableau
            Table table = new Table(UnitValue.createPercentArray(new float[]{22, 14, 24, 12, 28}))
                    .useAllAvailableWidth();

            // En-têtes
            String[] entetes = {"Date", "Type", "Produit", "Quantité", "Motif"};
            for (String e : entetes) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(e).setBold().setFontSize(10))
                        .setBackgroundColor(new DeviceRgb(44, 62, 80))
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER).setPadding(6));
            }

            // Lignes
            boolean alt = false;
            for (Mouvement m : liste) {
                com.itextpdf.kernel.colors.Color bg = alt
                        ? new DeviceRgb(245, 245, 245) : ColorConstants.WHITE;

                boolean isEntree = "ENTRÉE".equals(m.getType());
                DeviceRgb typeColor = isEntree
                        ? new DeviceRgb(30, 132, 73)
                        : new DeviceRgb(146, 43, 33);
                String typeTexte = isEntree ? "⬇ ENTRÉE" : "⬆ SORTIE";
                String qteTexte  = m.getQuantite() != null
                        ? (isEntree ? "+ " : "- ") + String.format("%.1f", m.getQuantite()) : "";

                String[] vals = {
                        m.getDateFormatee() != null ? m.getDateFormatee() : "",
                        "", // type géré séparément
                        m.getProduitNom() != null ? m.getProduitNom() : "",
                        qteTexte,
                        m.getMotif() != null ? m.getMotif() : ""
                };

                for (int i = 0; i < vals.length; i++) {
                    Cell cell;
                    if (i == 1) {
                        cell = new Cell().add(new Paragraph(typeTexte)
                                        .setFontSize(9).setBold().setFontColor(typeColor))
                                .setBackgroundColor(bg)
                                .setTextAlignment(TextAlignment.CENTER).setPadding(5);
                    } else {
                        cell = new Cell().add(new Paragraph(vals[i]).setFontSize(9))
                                .setBackgroundColor(bg)
                                .setTextAlignment(TextAlignment.CENTER).setPadding(5);
                    }
                    table.addCell(cell);
                }
                alt = !alt;
            }
            document.add(table);

            // Résumé
            long nbE = liste.stream().filter(m -> "ENTRÉE".equals(m.getType())).count();
            long nbS = liste.stream().filter(m -> "SORTIE".equals(m.getType())).count();
            document.add(new Paragraph(
                    "\nTotal : " + liste.size() + "   |   Entrées : " + nbE + "   |   Sorties : " + nbS)
                    .setFontSize(10).setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.GRAY).setMarginTop(10));

            document.close();
            showSuccess("✅ PDF exporté !\n\n📁 " + fichier.getAbsolutePath());

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'export PDF : " + e.getMessage());
        }
    }

    // =========================================================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès"); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}