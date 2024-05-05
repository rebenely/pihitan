module xyz.ravencrows.pihitan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
  requires com.google.gson;

  opens xyz.ravencrows.pihitan to javafx.fxml;
    exports xyz.ravencrows.pihitan;
    exports xyz.ravencrows.pihitan.userconfig;

  opens xyz.ravencrows.pihitan.templates to com.google.gson;
  opens xyz.ravencrows.pihitan.navigator to com.google.gson;

  exports xyz.ravencrows.pihitan.templates;
}