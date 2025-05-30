package javafxappescolar.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.modelo.dao.InicioDeSesionDAO;
import javafxappescolar.modelo.pojo.Usuario;
import javafxappescolar.utilidades.Utilidad;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void btnClicVerificarSesion(ActionEvent event) {
        String username = tfUsuario.getText();
        String password = tfPassword.getText();
        if(validarCampos(username, password)) {
            validarCredenciales(username, password);
        } 
    }
    
    private boolean validarCampos(String username, String password) {
        lbErrorPassword.setText("");
        lbErrorUsuario.setText("");
        boolean camposValidos = true;
        if (username.isEmpty()) {
            lbErrorUsuario.setText("Usuario requerido");
            camposValidos = false;
        } 
        if (password.isEmpty()) {
            lbErrorPassword.setText("Contraseña requerida");
            camposValidos = false;
        }
        return camposValidos;
    }
    
    private void validarCredenciales (String username, String password) {
        try {
            Usuario usuarioSesion = InicioDeSesionDAO.verificarCredenciales(username, password);
            if (usuarioSesion != null) {
                //TODO Flujo normal
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Credenciales correctas", usuarioSesion.toString() + 
                                " bienvenido(a) al sistema");
                irPantallaPrincipal(usuarioSesion);
            } else {
                //TODO Flujo alterno
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                        "Credenciales incorrectas", "Usuario y/o contraseña incorrectos, por favor verifica tu información");
            }
        } catch(SQLException ex) {
            //TODO Flujo excepción
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                        "Error", ex.getMessage());
        }     
    }
    
    private void irPantallaPrincipal (Usuario usuarioSesion) {
        try {
            Stage escenarioBase = (Stage) tfUsuario.getScene().getWindow();
            FXMLLoader cargador = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            
            FXMLPrincipalController controlador = cargador.getController();
            controlador.inicializarInformacion(usuarioSesion);
            
            Scene escenaPrincipal = new Scene(vista);
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Home");
            escenarioBase.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
