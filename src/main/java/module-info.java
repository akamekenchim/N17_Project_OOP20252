module com.wildlife {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.wildlife to javafx.fxml;
    exports com.wildlife;
}
