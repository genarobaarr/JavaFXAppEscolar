package javafxappescolar.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.interfaz.INotificacion;
import javafxappescolar.modelo.dao.AlumnoDAO;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.ResultadoOperacion;
import javafxappescolar.utilidades.Utilidad;

public class FXMLAdminAlumnoController implements Initializable, INotificacion {

    @FXML
    private TextField tfBuscar;
    @FXML
    private TableView<Alumno> tvAlumnos;
    @FXML
    private TableColumn colMatricula;
    @FXML
    private TableColumn colApPaterno;
    @FXML
    private TableColumn colApMaterno;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colFacultad;
    @FXML
    private TableColumn colCarrera;
    
    private ObservableList<Alumno> alumnos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    
    
    private void configurarTabla(){
        colMatricula.setCellValueFactory(new PropertyValueFactory("matricula"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colFacultad.setCellValueFactory(new PropertyValueFactory("facultad"));
        colCarrera.setCellValueFactory(new PropertyValueFactory("carrera"));
    }
    
    private void cargarInformacionTabla(){
        try {
            alumnos = FXCollections.observableArrayList();
            ArrayList<Alumno> alumnosDAO = AlumnoDAO.obtenerAlumnos();
            alumnos.addAll(alumnosDAO);
            tvAlumnos.setItems(alumnos);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los alumnos, por favor, inténtelo más tarde");
            cerrarVentana();
        }
    }
    
    private void cerrarVentana(){
        ((Stage) tfBuscar.getScene().getWindow()).close();
    }

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioAlumno(false, null);
    }

    @FXML
    private void btnClicModificar(ActionEvent event) {
        Alumno alumnoModificar = tvAlumnos.getSelectionModel().getSelectedItem();
        if (alumnoModificar != null) {
            irFormularioAlumno(true, alumnoModificar);
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selecciona un alumno", 
                    "Para modificar la información de un alumno, primero selecciona a alguno de la tabla.");
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        int pos = tvAlumnos.getSelectionModel().getSelectedIndex();
        if (pos >= 0) {
            Alumno alumno = alumnos.get(pos);
            String confirmacion = String.format("Eliminar alumno(a)", 
                    "¿Estás seguro de eliminar al alumno(a) %s %s?", 
                    alumno.getNombre(), alumno.getApellidoPaterno());
            if (Utilidad.mostrarAlertaConfirmacion("Eliminar alumno(a)", confirmacion)){
                eliminarAlumno(alumno.getIdAlumno());
            } else {
                
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selecciona un alumno", "Para eliminar la información de un alumno, primero selecciona a alguno de la tabla.");
        }
    }
    
    private void irFormularioAlumno(boolean esEdicion, Alumno alumnoEdicion) {
        try {
            Stage escenarioFormulario = new Stage();
            FXMLLoader cargador = new FXMLLoader(JavaFXAppEscolar. class.getResource("vista/FXMLFormularioAlumno.fxml"));
            Parent vista = cargador.load();
            
            //TODO Paso de parametros
            FXMLFormularioAlumnoController controlador = cargador.getController();
            controlador.iniciarlizarInformacion(esEdicion, alumnoEdicion, this);
            
            Scene escena = new Scene(vista);
            escenarioFormulario.setScene(escena);
            escenarioFormulario.setTitle("Formulario Alumno");
            escenarioFormulario.initModality(Modality.APPLICATION_MODAL);
            escenarioFormulario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void operacionExitosa(String tipo, String nombreAlumno) {
        System.out.println("Operacion: " + tipo+ ", con el alumno(a): " + nombreAlumno);
        cargarInformacionTabla();
    }
    
    private void eliminarAlumno(int idAlumno) {
        try {
            ResultadoOperacion resultado = AlumnoDAO.eliminarAlumno(idAlumno);
            if (!resultado.isError()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Alumno eliminado(a)", "El registro del alumno se ha elimado");
                cargarInformacionTabla();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Problemas al eliminar", resultado.getMensaje());
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Problemas al eliminar", "Lo sentimos :( por el momento no se puede llevar a cabo la acción");
        }
    }
}