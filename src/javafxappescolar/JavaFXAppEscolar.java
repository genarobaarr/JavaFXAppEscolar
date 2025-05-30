package javafxappescolar;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JavaFXAppEscolar extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("vista/FXMLInicioSesion.fxml"));
            Scene escenaInicioSesion = new Scene(vista);
            primaryStage.setScene(escenaInicioSesion);
            primaryStage.setTitle("Inicio de sesión");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
