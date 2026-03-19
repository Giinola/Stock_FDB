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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pkg.gestion_stock.dao.RestoDAO;
import pkg.gestion_stock.models.Produit;
import pkg.gestion_stock.models.Resto;
import pkg.gestion_stock.models.RestoItem;
import pkg.gestion_stock.models.StockManager;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RestoController {

    // ===== SAISIE =====
    @FXML private TableView<RestoItem> tableResto;
    @FXML private TableColumn<RestoItem, String> colCategorie;
    @FXML private TableColumn<RestoItem, String> colProduit;
    @FXML private TableColumn<RestoItem, Double> colTotal;
    @FXML private Label lblDate;

    // ===== HISTORIQUE =====
    @FXML private TableView<RestoItem> tableHistorique;
    @FXML private TableColumn<RestoItem, String> colHistCategorie;
    @FXML private TableColumn<RestoItem, String> colHistProduit;
    @FXML private TableColumn<RestoItem, Double> colHistTotal;
    @FXML private ComboBox<String> comboHistorique;

    private StockManager stockManager = StockManager.getInstance();
    private RestoDAO restoDAO = new RestoDAO();
    private ObservableList<RestoItem> restoItems      = FXCollections.observableArrayList();
    private ObservableList<RestoItem> historiqueItems = FXCollections.observableArrayList();
    private LocalDateTime dateHistoriqueSelectionne   = null;

    // Garder la liste des dates en mémoire pour retrouver la date exacte
    private List<LocalDateTime> datesList = new ArrayList<>();

    // =========================================================
    @FXML
    public void initialize() {
        System.out.println("✅ RestoController initialisé");

        restoDAO.creerTable();

        lblDate.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        configurerTableauSaisie();
        configurerTableauHistorique();
        chargerProduits();
        chargerListeDates();
    }

    // =========================================================
    // CONFIGURATION TABLEAUX
    // =========================================================
    private void configurerTableauSaisie() {
        colCategorie.setCellValueFactory(c -> c.getValue().categorieProperty());
        colProduit.setCellValueFactory(c -> c.getValue().produitProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());

        // Colonne TOTAL éditable + colorée
        colTotal.setCellFactory(col -> new TextFieldTableCell<>(new DoubleStringConverter()) {
            @Override
            public void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setStyle("");
                } else if (val <= 0) {
                    setStyle("-fx-text-fill: #aaa;");
                } else if (val <= 5) {
                    setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        // ✅ setOnEditCommit sur la COLONNE
        colTotal.setOnEditCommit(event ->
                event.getRowValue().setTotal(event.getNewValue())
        );

        // Style lignes
        tableResto.setRowFactory(tv -> new TableRow<RestoItem>() {
            @Override
            protected void updateItem(RestoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    setEditable(true);
                } else if (item.getEstTitreCategorie()) {
                    setStyle("-fx-background-color: #2980b9; -fx-font-weight: bold;");
                    setEditable(false);
                } else {
                    setStyle(getIndex() % 2 == 0
                            ? "-fx-background-color: white;"
                            : "-fx-background-color: #f8f9fa;");
                    setEditable(true);
                }
            }
        });

        tableResto.setItems(restoItems);
    }

    private void configurerTableauHistorique() {
        colHistCategorie.setCellValueFactory(c -> c.getValue().categorieProperty());
        colHistProduit.setCellValueFactory(c -> c.getValue().produitProperty());
        colHistTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());

        // Style lignes historique
        tableHistorique.setRowFactory(tv -> new TableRow<RestoItem>() {
            @Override
            protected void updateItem(RestoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getEstTitreCategorie()) {
                    setStyle("-fx-background-color: #2980b9; -fx-font-weight: bold;");
                } else {
                    setStyle(getIndex() % 2 == 0
                            ? "-fx-background-color: white;"
                            : "-fx-background-color: #f8f9fa;");
                }
            }
        });

        // Colonne total colorée
        colHistTotal.setCellFactory(col -> new TableCell<RestoItem, Double>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(String.format("%.1f", val));
                if (val <= 0)      setStyle("-fx-text-fill: #aaa;");
                else if (val <= 5) setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                else               setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
        });

        tableHistorique.setItems(historiqueItems);
    }

    // =========================================================
    // CHARGEMENT DONNÉES
    // =========================================================
    private void chargerProduits() {
        restoItems.clear();
        String categorieActuelle = "";

        for (Produit p : stockManager.getProduits()) {
            if (p.getNom() == null) continue;

            if (!p.getCategorie().equals(categorieActuelle)) {
                restoItems.add(new RestoItem(p.getCategorie(), null, null));
                categorieActuelle = p.getCategorie();
            }
            restoItems.add(new RestoItem(p.getCategorie(), p.getNom(), 0.0));
        }
    }

    private void chargerListeDates() {
        // ✅ Garder les dates en mémoire pour retrouver la date exacte plus tard
        datesList = restoDAO.findDatesComptage();

        ObservableList<String> dateStrings = FXCollections.observableArrayList();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (LocalDateTime d : datesList) {
            dateStrings.add(d.format(fmt));
        }
        comboHistorique.setItems(dateStrings);
    }

    // =========================================================
    // ACTIONS
    // =========================================================
    @FXML
    private void enregistrer() {
        LocalDateTime maintenant = LocalDateTime.now();
        List<Resto> comptages = new ArrayList<>();

        for (RestoItem item : restoItems) {
            if (!item.getEstTitreCategorie() && item.getTotal() > 0) {
                comptages.add(new Resto(
                        item.getCategorie(),
                        item.getProduit(),
                        item.getTotal()
                ));
            }
        }

        if (comptages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune donnée",
                    "Veuillez saisir au moins une quantité supérieure à 0.");
            return;
        }

        restoDAO.sauvegarderComptage(comptages, maintenant);
        chargerListeDates();

        showAlert(Alert.AlertType.INFORMATION, "Enregistrement réussi",
                "✅ " + comptages.size() + " produit(s) enregistrés pour le " +
                        maintenant.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    @FXML
    private void reinitialiser() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Réinitialiser");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Remettre toutes les quantités à 0 ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            for (RestoItem item : restoItems) {
                if (!item.getEstTitreCategorie()) item.setTotal(0.0);
            }
            tableResto.refresh();
        }
    }

    @FXML
    private void chargerHistorique() {
        String dateStr = comboHistorique.getValue();
        if (dateStr == null) return;

        // ✅ Retrouver la date exacte depuis datesList (évite les problèmes de parsing)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dateTrouvee = null;

        for (LocalDateTime d : datesList) {
            if (d.format(fmt).equals(dateStr)) {
                dateTrouvee = d;
                break;
            }
        }

        if (dateTrouvee == null) {
            System.out.println("❌ Date non trouvée : " + dateStr);
            return;
        }

        dateHistoriqueSelectionne = dateTrouvee;
        List<Resto> comptages = restoDAO.findByDate(dateTrouvee);
        System.out.println("📥 Comptages trouvés : " + comptages.size());

        historiqueItems.clear();
        String categorieActuelle = "";
        for (Resto r : comptages) {
            if (!r.getCategorie().equals(categorieActuelle)) {
                historiqueItems.add(new RestoItem(r.getCategorie(), null, null));
                categorieActuelle = r.getCategorie();
            }
            historiqueItems.add(new RestoItem(
                    r.getCategorie(), r.getProduit(), r.getQuantite()
            ));
        }

        tableHistorique.refresh();
    }

    @FXML
    private void supprimerComptage() {
        String dateStr = comboHistorique.getValue();
        if (dateStr == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Sélectionnez un comptage à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer le comptage du " + dateStr + " ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // ✅ Retrouver la date exacte
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTrouvee = null;
            for (LocalDateTime d : datesList) {
                if (d.format(fmt).equals(dateStr)) {
                    dateTrouvee = d;
                    break;
                }
            }
            if (dateTrouvee != null) {
                restoDAO.supprimerComptage(dateTrouvee);
            }
            chargerListeDates();
            historiqueItems.clear();
            dateHistoriqueSelectionne = null;
        }
    }

    // =========================================================
    // EXPORT EXCEL
    // =========================================================
    @FXML
    private void exporterExcel() {
        List<RestoItem> liste = getListeAExporter();
        if (liste == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le fichier Excel");
        fc.setInitialFileName("comptage_resto.xlsx");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Excel", "*.xlsx"));
        File fichier = fc.showSaveDialog(tableResto.getScene().getWindow());
        if (fichier == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Comptage RESTO");

            // Style en-tête
            CellStyle styleEntete = workbook.createCellStyle();
            styleEntete.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fontEntete = workbook.createFont();
            fontEntete.setColor(IndexedColors.WHITE.getIndex());
            fontEntete.setBold(true);
            styleEntete.setFont(fontEntete);
            styleEntete.setAlignment(HorizontalAlignment.CENTER);

            // Style catégorie
            CellStyle styleCategorie = workbook.createCellStyle();
            styleCategorie.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            styleCategorie.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fontCat = workbook.createFont();
            fontCat.setBold(true);
            styleCategorie.setFont(fontCat);

            // Style lignes pair / impair
            CellStyle stylePair   = workbook.createCellStyle();
            CellStyle styleImpair = workbook.createCellStyle();
            styleImpair.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            styleImpair.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // En-têtes
            Row rowEntete = sheet.createRow(0);
            String[] entetes = {"Catégorie", "Produit", "Total"};
            for (int i = 0; i < entetes.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = rowEntete.createCell(i);
                cell.setCellValue(entetes[i]);
                cell.setCellStyle(styleEntete);
            }

            // Données
            int rowNum = 1;
            int produitIndex = 0;
            for (RestoItem item : liste) {
                Row row = sheet.createRow(rowNum++);
                if (item.getEstTitreCategorie()) {
                    org.apache.poi.ss.usermodel.Cell c0 = row.createCell(0);
                    c0.setCellValue(item.getCategorie());
                    c0.setCellStyle(styleCategorie);
                    row.createCell(1).setCellStyle(styleCategorie);
                    row.createCell(2).setCellStyle(styleCategorie);
                } else {
                    CellStyle styleRow = produitIndex % 2 == 0 ? stylePair : styleImpair;
                    org.apache.poi.ss.usermodel.Cell c0 = row.createCell(0);
                    c0.setCellValue(item.getCategorie() != null ? item.getCategorie() : "");
                    c0.setCellStyle(styleRow);
                    org.apache.poi.ss.usermodel.Cell c1 = row.createCell(1);
                    c1.setCellValue(item.getProduit() != null ? item.getProduit() : "");
                    c1.setCellStyle(styleRow);
                    org.apache.poi.ss.usermodel.Cell c2 = row.createCell(2);
                    c2.setCellValue(item.getTotal() != null ? item.getTotal() : 0.0);
                    c2.setCellStyle(styleRow);
                    produitIndex++;
                }
            }

            for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                workbook.write(fos);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "✅ Excel exporté !\n\n📁 " + fichier.getAbsolutePath());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur Excel : " + e.getMessage());
        }
    }

    // =========================================================
    // EXPORT PDF
    // =========================================================
    @FXML
    private void exporterPDF() {
        List<RestoItem> liste = getListeAExporter();
        if (liste == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le fichier PDF");
        fc.setInitialFileName("comptage_resto.pdf");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File fichier = fc.showSaveDialog(tableResto.getScene().getWindow());
        if (fichier == null) return;

        try {
            PdfWriter writer   = new PdfWriter(fichier.getAbsolutePath());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document  = new Document(pdfDoc);

            // Titre
            String dateTitle = dateHistoriqueSelectionne != null
                    ? dateHistoriqueSelectionne.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            document.add(new Paragraph("Comptage RESTO — " + dateTitle)
                    .setFontSize(16).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Tableau
            Table table = new Table(
                    UnitValue.createPercentArray(new float[]{30, 50, 20}))
                    .useAllAvailableWidth();

            // En-têtes
            for (String h : new String[]{"Catégorie", "Produit", "Total"}) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(h).setBold().setFontSize(10))
                        .setBackgroundColor(new DeviceRgb(41, 128, 185))
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6));
            }

            // Lignes
            boolean alt = false;
            for (RestoItem item : liste) {
                if (item.getEstTitreCategorie()) {
                    table.addCell(new Cell(1, 3)
                            .add(new Paragraph(item.getCategorie())
                                    .setBold().setFontSize(10))
                            .setBackgroundColor(new DeviceRgb(52, 152, 219))
                            .setFontColor(ColorConstants.WHITE)
                            .setPadding(5));
                } else {
                    DeviceRgb bg = alt
                            ? new DeviceRgb(248, 249, 250)
                            : new DeviceRgb(255, 255, 255);

                    double total = item.getTotal() != null ? item.getTotal() : 0.0;
                    DeviceRgb totalColor = total <= 0
                            ? new DeviceRgb(170, 170, 170)
                            : total <= 5
                            ? new DeviceRgb(230, 126, 34)
                            : new DeviceRgb(39, 174, 96);

                    table.addCell(new Cell()
                            .add(new Paragraph(
                                    item.getCategorie() != null ? item.getCategorie() : "")
                                    .setFontSize(9))
                            .setBackgroundColor(bg).setPadding(5));

                    table.addCell(new Cell()
                            .add(new Paragraph(
                                    item.getProduit() != null ? item.getProduit() : "")
                                    .setFontSize(9))
                            .setBackgroundColor(bg).setPadding(5));

                    table.addCell(new Cell()
                            .add(new Paragraph(String.format("%.1f", total))
                                    .setFontSize(9).setBold().setFontColor(totalColor))
                            .setBackgroundColor(bg)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(5));

                    alt = !alt;
                }
            }

            document.add(table);

            // Résumé
            long nbProduits = liste.stream()
                    .filter(i -> !i.getEstTitreCategorie()).count();
            long nbNonZero  = liste.stream()
                    .filter(i -> !i.getEstTitreCategorie() && i.getTotal() > 0).count();

            document.add(new Paragraph(
                    "\nTotal produits : " + nbProduits +
                            "   |   Produits avec reste : " + nbNonZero)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(10));

            document.close();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "✅ PDF exporté !\n\n📁 " + fichier.getAbsolutePath());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur PDF : " + e.getMessage());
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private List<RestoItem> getListeAExporter() {
        if (!historiqueItems.isEmpty()) {
            return new ArrayList<>(historiqueItems);
        }

        boolean hasDonnees = restoItems.stream()
                .anyMatch(i -> !i.getEstTitreCategorie() && i.getTotal() > 0);

        if (!hasDonnees) {
            showAlert(Alert.AlertType.WARNING, "Aucune donnée",
                    "Saisissez des quantités ou sélectionnez un historique avant d'exporter.");
            return null;
        }

        return new ArrayList<>(restoItems);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}