module com.example.gestion_stock {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;

    opens pkg.gestion_stock to javafx.fxml;
    opens pkg.gestion_stock.controllers to javafx.fxml;
    opens pkg.gestion_stock.models to javafx.base;

    exports pkg.gestion_stock;
}