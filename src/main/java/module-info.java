module pkg.stock_fdb {

    // ===== JavaFX =====
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires org.controlsfx.controls;

    // ===== Base de données =====
    requires java.sql;
    requires org.postgresql.jdbc;

    // ===== iTextPDF =====
    requires kernel;
    requires layout;
    requires io;

    // ===== Apache POI (Excel) =====
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    // ===== Ouvre les packages pour JavaFX =====
    opens pkg.gestion_stock to javafx.fxml;
    opens pkg.gestion_stock.controllers to javafx.fxml;
    opens pkg.gestion_stock.models to javafx.base;
    opens pkg.stock_fdb to javafx.fxml;

    // ===== Exporte les packages =====
    exports pkg.gestion_stock;
    exports pkg.gestion_stock.Database;
    exports pkg.stock_fdb;
}