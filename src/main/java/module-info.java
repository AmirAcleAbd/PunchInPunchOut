module punchit.punchinpunchout {
    requires javafx.controls;
    requires javafx.fxml;


    opens punchit.punchinpunchout to javafx.fxml;
    exports punchit.punchinpunchout;
    exports punchit.punchinpunchout.QueryCenter;
    opens punchit.punchinpunchout.QueryCenter to javafx.fxml;
}