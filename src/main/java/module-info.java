module ru.volkov.cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.sql;
    requires java.logging;
    requires org.postgresql.jdbc;

    exports ru.volkov.cw.model;
    opens ru.volkov.cw.model to javafx.base;
    opens ru.volkov.cw to javafx.fxml;
    exports ru.volkov.cw;
    exports util;
    opens util to javafx.fxml;
    exports ru.volkov.cw.dao;
    opens ru.volkov.cw.dao to javafx.fxml;
    exports ru.volkov.cw.controller;
    opens ru.volkov.cw.controller to javafx.fxml;
}