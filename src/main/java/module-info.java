module xyz.ravencrows.pihitan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens xyz.ravencrows.pihitan to javafx.fxml;
    exports xyz.ravencrows.pihitan;
    exports xyz.ravencrows.pihitan.userconfig;
}