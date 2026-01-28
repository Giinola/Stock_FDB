module pkg.stock_fdb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens pkg.stock_fdb to javafx.fxml;
    exports pkg.stock_fdb;
}