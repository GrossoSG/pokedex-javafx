module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.testes;
    opens org.example.testes to javafx.fxml;
    exports org.example.controllers;
    opens org.example.controllers to javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires javafx.media;
}
