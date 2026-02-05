package pkg.gestion_stock.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StockManager {

    private static StockManager instance;

    private ObservableList<Produit> produits = FXCollections.observableArrayList();
    private ObservableList<Mouvement> mouvements = FXCollections.observableArrayList();

    // Singleton
    private StockManager() {
        chargerProduits();
    }

    public static StockManager getInstance() {
        if (instance == null) {
            instance = new StockManager();
        }
        return instance;
    }

    // Charger les produits (même liste que dans ProduitController)
    private void chargerProduits() {
        produits.clear();

        // BEVERAGES
        produits.add(new Produit("BEVERAGES", null, null));
        produits.add(new Produit("BEVERAGES", "PEARONA", null));
        produits.add(new Produit("BEVERAGES", "ORANGINA", null));
        produits.add(new Produit("BEVERAGES", "ESKI VANILLE", 19.0));
        produits.add(new Produit("BEVERAGES", "ESKI STRAWBERRY", 24.0));
        produits.add(new Produit("BEVERAGES", "ESKI VERT", null));
        produits.add(new Produit("BEVERAGES", "CIDONA", null));
        produits.add(new Produit("BEVERAGES", "CRYSTAL", 23.0));
        produits.add(new Produit("BEVERAGES", "BE-EAU", 75.0));

        // MEAT
        produits.add(new Produit("MEAT", null, null));
        produits.add(new Produit("MEAT", "LAMB KEBAB", 2.0));
        produits.add(new Produit("MEAT", "CHICKEN KEBAB", 4.0));
        produits.add(new Produit("MEAT", "MINCED LAMB MEAT", 16.0));
        produits.add(new Produit("MEAT", "CHIPS", 50.0));
        produits.add(new Produit("MEAT", "FALAFEL", 1.5));

        // DAIRY PRODUCT
        produits.add(new Produit("DAIRY PRODUCT", null, null));
        produits.add(new Produit("DAIRY PRODUCT", "CHEDDAR", 0.0));
        produits.add(new Produit("DAIRY PRODUCT", "MOZARELLA", 63.0));
        produits.add(new Produit("DAIRY PRODUCT", "MILK", 0.0));

        // DRY ITEMS
        produits.add(new Produit("DRY ITEMS", null, null));
        produits.add(new Produit("DRY ITEMS", "WRAP", 4.0));
        produits.add(new Produit("DRY ITEMS", "PIZZA FLOUR", null));
        produits.add(new Produit("DRY ITEMS", "YEAST", null));
        produits.add(new Produit("DRY ITEMS", "SEMOULINA", null));
        produits.add(new Produit("DRY ITEMS", "SALT", 64.0));
        produits.add(new Produit("DRY ITEMS", "BLACK SESAME", 11.0));
        produits.add(new Produit("DRY ITEMS", "SUGAR", 0.0));
        produits.add(new Produit("DRY ITEMS", "AROMAT SEASONING POWDER", 3.0));
        produits.add(new Produit("DRY ITEMS", "CUMIN POWDER", 0.0));

        // SAUCES
        produits.add(new Produit("SAUCES", null, null));
        produits.add(new Produit("SAUCES", "KETCHUP", 0.0));
        produits.add(new Produit("SAUCES", "MAYONNAISE", 4.0));
        produits.add(new Produit("SAUCES", "TAHINI", 42.0));
        produits.add(new Produit("SAUCES", "POIS CHICHE", 36.0));
        produits.add(new Produit("SAUCES", "SACHET KETCHUP", 100.0));
        produits.add(new Produit("SAUCES", "SACHET MAYONNAISE", null));

        // LIQUID ITEM
        produits.add(new Produit("LIQUID ITEM", null, null));
        produits.add(new Produit("LIQUID ITEM", "VINEGAR", null));
        produits.add(new Produit("LIQUID ITEM", "FRYING OIL", 0.0));
        produits.add(new Produit("LIQUID ITEM", "SOYA BEAN OIL", 75.0));
        produits.add(new Produit("LIQUID ITEM", "POMACE OLIVE OIL", 60.0));

        // VEGETABLES
        produits.add(new Produit("VEGETABLES", null, null));
        produits.add(new Produit("VEGETABLES", "LETTUCE", 0.0));
        produits.add(new Produit("VEGETABLES", "TOMATO", 0.0));
        produits.add(new Produit("VEGETABLES", "ONIONS", null));
        produits.add(new Produit("VEGETABLES", "CHILLI", 0.0));
        produits.add(new Produit("VEGETABLES", "CITRON", null));
        produits.add(new Produit("VEGETABLES", "GARLIC", 0.0));

        // CLEANING PRODUCTS
        produits.add(new Produit("CLEANING PRODUCTS", null, null));
        produits.add(new Produit("CLEANING PRODUCTS", "JAVEL", 35.0));
        produits.add(new Produit("CLEANING PRODUCTS", "DETERGENT", null));
        produits.add(new Produit("CLEANING PRODUCTS", "WINDOW CLEANER", 0.0));
        produits.add(new Produit("CLEANING PRODUCTS", "SANITIZER", null));
        produits.add(new Produit("CLEANING PRODUCTS", "ORA SOAP", 7.0));
        produits.add(new Produit("CLEANING PRODUCTS", "VILEDA SPONGE", 0.0));
        produits.add(new Produit("CLEANING PRODUCTS", "INOX PINKI", 1.0));
        produits.add(new Produit("CLEANING PRODUCTS", "BIN BAGS (50L)", 14.0));
        produits.add(new Produit("CLEANING PRODUCTS", "CREST", 20.0));

        // OTHER ITEMS
        produits.add(new Produit("OTHER ITEMS", null, null));
        produits.add(new Produit("OTHER ITEMS", "SMALL TAKE AWAY", 1000.0));
        produits.add(new Produit("OTHER ITEMS", "BIG TAKE AWAY", 4000.0));
        produits.add(new Produit("OTHER ITEMS", "PIDE BOX", 0.0));
        produits.add(new Produit("OTHER ITEMS", "TRAY", 600.0));
        produits.add(new Produit("OTHER ITEMS", "CHIPS BAG", 4200.0));
        produits.add(new Produit("OTHER ITEMS", "PAPER  ROLL", 0.0));
        produits.add(new Produit("OTHER ITEMS", "CLING FILM", 6.0));
        produits.add(new Produit("OTHER ITEMS", "SAUCE CUP", 500.0));
        produits.add(new Produit("OTHER ITEMS", "STRAW", 700.0));
        produits.add(new Produit("OTHER ITEMS", "TISSUE", 3.0));
        produits.add(new Produit("OTHER ITEMS", "GLOVES M (1 carton)", 9.0));
        produits.add(new Produit("OTHER ITEMS", "GLOVES  L (1 carton)", 14.0));
        produits.add(new Produit("OTHER ITEMS", "GLOVES XL (1 carton )", 13.0));
        produits.add(new Produit("OTHER ITEMS", "HAIRNET", 300.0));
        produits.add(new Produit("OTHER ITEMS", "WOOD", 8.0));
        produits.add(new Produit("OTHER ITEMS", "THERMAL PAPER", 62.0));
        produits.add(new Produit("OTHER ITEMS", "BLACK CLOTH", null));
    }

    // Ajouter une entrée en stock
    public void ajouterEntree(Produit produit, Double quantite, String motif) {
        if (produit != null && quantite != null && quantite > 0) {
            // Mettre à jour la quantité
            Double ancienneQte = produit.getQtyInStock();
            if (ancienneQte == null) ancienneQte = 0.0;
            produit.setQtyInStock(ancienneQte + quantite);

            // Enregistrer le mouvement
            Mouvement mouvement = new Mouvement("ENTRÉE", produit.getNom(), quantite, motif);
            mouvements.add(0, mouvement); // Ajouter au début de la liste

            System.out.println("✅ Entrée ajoutée : " + produit.getNom() + " +" + quantite);
        }
    }

    // Getters
    public ObservableList<Produit> getProduits() {
        return produits;
    }

    public ObservableList<Mouvement> getMouvements() {
        return mouvements;
    }

    // Récupérer uniquement les produits (pas les titres de catégories)
    public ObservableList<Produit> getProduitsUniquement() {
        ObservableList<Produit> liste = FXCollections.observableArrayList();
        for (Produit p : produits) {
            if (p.getNom() != null) {  // Exclure les titres de catégories
                liste.add(p);
            }
        }
        return liste;
    }
}