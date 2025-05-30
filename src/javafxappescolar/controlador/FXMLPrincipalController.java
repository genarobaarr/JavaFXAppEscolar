package javafxappescolar.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.modelo.pojo.Usuario;
import javafxappescolar.utilidades.Utilidad;

public class FXMLPrincipalController implements Initializable {
    
    private Usuario usuarioSesion;
    @FXML
    private Label lbNombre;
    @FXML
    private Label lbUsuario;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void inicializarInformacion(Usuario usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
        cargarInformacionUsuario();
    }
    
    private void cargarInformacionUsuario() {
        if(usuarioSesion != null) {
            lbNombre.setText(usuarioSesion.toString());
            lbUsuario.setText(usuarioSesion.getUsername());
        }
    }

    @FXML
    private void btnClicCerrarSesion(ActionEvent event) {
        try {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Cerraste sesión", 
                        usuarioSesion.toString() + " cerraste sesión exitosamente");
            Stage escenarioBase = (Stage) lbNombre.getScene().getWindow();
            escenarioBase.setScene(new Scene(FXMLLoader.load(JavaFXAppEscolar.class.getResource("vista/FXMLInicioSesion.fxml"))));
            escenarioBase.setTitle("Inicio de sesión");
            escenarioBase.showAndWait();
            usuarioSesion = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicBtnAdminAlumnos(ActionEvent event) {
        try {
            Stage escenarioAdmin = new Stage();
            Parent vista = FXMLLoader.load(JavaFXAppEscolar.class.getResource("vista/FXMLAdminAlumno.fxml"));
            Scene escena = new Scene(vista);
            escenarioAdmin.setScene(escena);
            escenarioAdmin.setTitle("Administrador de Alumnos");
            escenarioAdmin.initModality(Modality.APPLICATION_MODAL);
            escenarioAdmin.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
}
