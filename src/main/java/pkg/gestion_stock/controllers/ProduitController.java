package pkg.gestion_stock.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pkg.gestion_stock.models.Produit;
import javafx.scene.control.TableCell;

public class ProduitController {

    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colQuantite;

    private final ObservableList<Produit> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("=== DÉBUT INITIALIZE ===");

        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // Gestion propre de la quantité (null → vide)
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("qtyInStock"));
        colQuantite.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("—");  // ou "" si tu préfères vide
                } else {
                    setText(String.format("%.1f", item));  // 1 décimale, ou "%.0f" pour entier
                }
            }
        });

        // Style catégories (titre gras + fond gris)
        tableProduits.setRowFactory(tv -> new TableRow<Produit>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getNom() == null) {
                    setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-font-size: 13;");
                } else {
                    setStyle("");
                }
            }
        });

        tableProduits.setItems(data);
        tableProduits.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // colonnes s'adaptent

        chargerDonnees(); // charge au démarrage

        System.out.println("Nombre de produits chargés : " + data.size());
        System.out.println("=== FIN INITIALIZE ===");
    }

    private void chargerDonnees() {
        data.clear();

        // BEVERAGES
        data.add(new Produit("BEVERAGES", null, null)); // titre catégorie
        data.add(new Produit("BEVERAGES", "PEARONA", null));
        data.add(new Produit("BEVERAGES", "ORANGINA", null));
        data.add(new Produit("BEVERAGES", "ESKI VANILLE", 19.0));
        data.add(new Produit("BEVERAGES", "ESKI STRAWBERRY", 24.0));
        data.add(new Produit("BEVERAGES", "ESKI VERT", null));
        data.add(new Produit("BEVERAGES", "CIDONA", null));
        data.add(new Produit("BEVERAGES", "CRYSTAL", 23.0));
        data.add(new Produit("BEVERAGES", "BE-EAU", 75.0));

        // MEAT
        data.add(new Produit("MEAT", null, null));
        data.add(new Produit("MEAT", "LAMB KEBAB", 2.0));
        data.add(new Produit("MEAT", "CHICKEN KEBAB", 4.0));
        data.add(new Produit("MEAT", "MINCED LAMB MEAT", 16.0));
        data.add(new Produit("MEAT", "CHIPS", 50.0));
        data.add(new Produit("MEAT", "FALAFEL", 1.5));

        // DAIRY PRODUCT
        data.add(new Produit("DAIRY PRODUCT", null, null));
        data.add(new Produit("DAIRY PRODUCT", "CHEDDAR", 0.0));
        data.add(new Produit("DAIRY PRODUCT", "MOZARELLA", 63.0));
        data.add(new Produit("DAIRY PRODUCT", "MILK", 0.0));

        // DRY ITEMS
        data.add(new Produit("DRY ITEMS", null, null));
        data.add(new Produit("DRY ITEMS", "WRAP", 4.0));
        data.add(new Produit("DRY ITEMS", "PIZZA FLOUR", null));
        data.add(new Produit("DRY ITEMS", "YEAST", null));
        data.add(new Produit("DRY ITEMS", "SEMOULINA", null));
        data.add(new Produit("DRY ITEMS", "SALT", 64.0));
        data.add(new Produit("DRY ITEMS", "BLACK SESAME", 11.0));
        data.add(new Produit("DRY ITEMS", "SUGAR", 0.0));
        data.add(new Produit("DRY ITEMS", "AROMAT SEASONING POWDER", 3.0));
        data.add(new Produit("DRY ITEMS", "CUMIN POWDER", 0.0));

        // SAUCES
        data.add(new Produit("SAUCES", null, null));
        data.add(new Produit("SAUCES", "KETCHUP", 0.0));
        data.add(new Produit("SAUCES", "MAYONNAISE", 4.0));
        data.add(new Produit("SAUCES", "TAHINI", 42.0));
        data.add(new Produit("SAUCES", "POIS CHICHE", 36.0));
        data.add(new Produit("SAUCES", "SACHET KETCHUP", 100.0));
        data.add(new Produit("SAUCES", "SACHET MAYONNAISE", null));

        // LIQUID ITEM
        data.add(new Produit("LIQUID ITEM", null, null));
        data.add(new Produit("LIQUID ITEM", "VINEGAR", null));
        data.add(new Produit("LIQUID ITEM", "FRYING OIL", 0.0));
        data.add(new Produit("LIQUID ITEM", "SOYA BEAN OIL", 75.0));
        data.add(new Produit("LIQUID ITEM", "POMACE OLIVE OIL", 60.0));

        // VEGETABLES
        data.add(new Produit("VEGETABLES", null, null));
        data.add(new Produit("VEGETABLES", "LETTUCE", 0.0));
        data.add(new Produit("VEGETABLES", "TOMATO", 0.0));
        data.add(new Produit("VEGETABLES", "ONIONS", null));
        data.add(new Produit("VEGETABLES", "CHILLI", 0.0));
        data.add(new Produit("VEGETABLES", "CITRON", null));
        data.add(new Produit("VEGETABLES", "GARLIC", 0.0));

        // CLEANING PRODUCTS
        data.add(new Produit("CLEANING PRODUCTS", null, null));
        data.add(new Produit("CLEANING PRODUCTS", "JAVEL", 35.0));
        data.add(new Produit("CLEANING PRODUCTS", "DETERGENT", null));
        data.add(new Produit("CLEANING PRODUCTS", "WINDOW CLEANER", 0.0));
        data.add(new Produit("CLEANING PRODUCTS", "SANITIZER", null));
        data.add(new Produit("CLEANING PRODUCTS", "ORA SOAP", 7.0));
        data.add(new Produit("CLEANING PRODUCTS", "VILEDA SPONGE", 0.0));
        data.add(new Produit("CLEANING PRODUCTS", "INOX PINKI", 1.0));
        data.add(new Produit("CLEANING PRODUCTS", "BIN BAGS (50L)", 14.0));
        data.add(new Produit("CLEANING PRODUCTS", "CREST", 20.0));

        // OTHER ITEMS
        data.add(new Produit("OTHER ITEMS", null, null));
        data.add(new Produit("OTHER ITEMS", "SMALL TAKE AWAY", 1000.0));
        data.add(new Produit("OTHER ITEMS", "BIG TAKE AWAY", 4000.0));
        data.add(new Produit("OTHER ITEMS", "PIDE BOX", 0.0));
        data.add(new Produit("OTHER ITEMS", "TRAY", 600.0));
        data.add(new Produit("OTHER ITEMS", "CHIPS BAG", 4200.0));
        data.add(new Produit("OTHER ITEMS", "PAPER  ROLL", 0.0));
        data.add(new Produit("OTHER ITEMS", "CLING FILM", 6.0));
        data.add(new Produit("OTHER ITEMS", "SAUCE CUP", 500.0));
        data.add(new Produit("OTHER ITEMS", "STRAW", 700.0));
        data.add(new Produit("OTHER ITEMS", "TISSUE", 3.0));
        data.add(new Produit("OTHER ITEMS", "GLOVES M (1 carton)", 9.0));
        data.add(new Produit("OTHER ITEMS", "GLOVES  L (1 carton)", 14.0));
        data.add(new Produit("OTHER ITEMS", "GLOVES XL (1 carton )", 13.0));
        data.add(new Produit("OTHER ITEMS", "HAIRNET", 300.0));
        data.add(new Produit("OTHER ITEMS", "WOOD", 8.0));
        data.add(new Produit("OTHER ITEMS", "THERMAL PAPER", 62.0));
        data.add(new Produit("OTHER ITEMS", "BLACK CLOTH", null));
    }

    @FXML
    private void actualiser() {
        chargerDonnees();
        System.out.println("✓ Actualisation effectuée");
    }
}