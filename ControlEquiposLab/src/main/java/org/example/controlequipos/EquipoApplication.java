package org.example.controlequipos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controlequipos.database.JPAUtil;

import java.io.IOException;

public class EquipoApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EquipoApplication.class.getResource("Equipos-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Control de Equipos de Laboratorio - Hibernate ORM");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        JPAUtil.cerrar();
    }
}