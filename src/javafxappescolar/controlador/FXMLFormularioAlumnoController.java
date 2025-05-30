package javafxappescolar.controlador;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafxappescolar.dominio.AlumnoDM;
import javafxappescolar.interfaz.INotificacion;
import javafxappescolar.modelo.dao.AlumnoDAO;
import javafxappescolar.modelo.dao.CatalogoDAO;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.Carrera;
import javafxappescolar.modelo.pojo.Facultad;
import javafxappescolar.modelo.pojo.ResultadoOperacion;
import javafxappescolar.utilidades.Utilidad;
import javax.imageio.ImageIO;

public class FXMLFormularioAlumnoController implements Initializable {

    @FXML
    private ImageView ivFoto;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfMatricula;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private ComboBox<Facultad> cbFacultad;
    @FXML
    private ComboBox<Carrera> cbCarrera;
    
    ObservableList<Facultad> facultades;
    ObservableList<Carrera> carreras;
    File archivoFoto;
    INotificacion observador;
    Alumno alumnoEdicion;
    boolean esEdicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarFacultades();
        seleccionarFacultad();
    }
    
    public void iniciarlizarInformacion(boolean esEdicion, Alumno alumnoEdicion, INotificacion observador) {
        this.esEdicion = esEdicion;
        this.alumnoEdicion = alumnoEdicion;
        this.observador = observador;
        if (esEdicion) {
            cargarInformacionEdicion();
        }
    }
    
    private void cargarInformacionEdicion() {
        tfMatricula.setText(alumnoEdicion.getMatricula());
        tfApellidoPaterno.setText(alumnoEdicion.getApellidoPaterno());
        tfApellidoMaterno.setText(alumnoEdicion.getApellidoMaterno());
        tfEmail.setText(alumnoEdicion.getEmail());
        tfNombre.setText(alumnoEdicion.getNombre());
        if (alumnoEdicion.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(LocalDate.parse(alumnoEdicion.getFechaNacimiento().toString()));
        }
        tfMatricula.setDisable(true);
        
        int indiceFacultad = obtenerPosicionFacultad(alumnoEdicion.getIdFacultad());
        cbFacultad.getSelectionModel().select(indiceFacultad);
        int indiceCarrera = obtenerPosicionCarrera(alumnoEdicion.getIdCarrera());
        cbCarrera.getSelectionModel().select(indiceCarrera);
        
        try {
            byte[] foto = AlumnoDAO.obtenerFotoAlumno(alumnoEdicion.getIdAlumno());
            alumnoEdicion.setFoto(foto);
            ByteArrayInputStream input = new ByteArrayInputStream(foto);
            Image image = new Image(input);
            ivFoto.setImage(image);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarFacultades(){
        try {
            facultades = FXCollections.observableArrayList();
            List<Facultad> facultadesDAO = CatalogoDAO.obtenerFacultades();
            facultades.addAll(facultadesDAO);
            cbFacultad.setItems(facultades);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de las facultades, por favor, inténtelo más tarde");
            cerrarVentana();
        }
    }
    
    private void seleccionarFacultad() {
        cbFacultad.valueProperty().addListener(new ChangeListener<Facultad>() {
            @Override
            public void changed(ObservableValue<? extends Facultad> observable, Facultad oldValue, Facultad newValue) {
                if (newValue != null) {
                    cargarCarreras(newValue.getIdFacultad());
                }
            }
        });
    }
    
    private void cargarCarreras(int idFacultad) {
        try {
            carreras = FXCollections.observableArrayList();
            List<Carrera> carrerasDAO = CatalogoDAO.obtenerCarrerasPorFacultad(idFacultad);
            carreras.addAll(carrerasDAO);
            cbCarrera.setItems(carreras);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de las carreras, por favor, inténtelo más tarde");
            cerrarVentana();
        }
    }
    
    private void cerrarVentana() {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }

    @FXML
    private void btnClicSeleccionarFoto(ActionEvent event) {
        mostrarDialogoSeleccionFoto();
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            try {
                if(!esEdicion) {
                    ResultadoOperacion resultado = AlumnoDM.verificarEstadoMatricula(tfMatricula.getText());
                    if (!resultado.isError()) {
                        Alumno alumno = obtenerAlumnoNuevo();
                        guardarAlumno(alumno);
                    } else {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Verificar datos", resultado.getMensaje());
                    }
                } else {
                    Alumno alumno = obtenerAlumnoEdicion();
                    modificarAlumno(alumno);
                }
            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en foto", 
                        "Lo sentimos la foto seleccionada no puede ser guardada");
            }
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
    }
    
    private void mostrarDialogoSeleccionFoto() {
        FileChooser dialogoSeleccion = new FileChooser();
        dialogoSeleccion.setTitle("Selecciona una foto");
        FileChooser.ExtensionFilter filtroImg = new FileChooser.ExtensionFilter("Archivos de imagen", "*.jpg", "*.jpeg", "*.png");
        dialogoSeleccion.getExtensionFilters().add(filtroImg);
        archivoFoto = dialogoSeleccion.showOpenDialog(Utilidad.getEscenario(tfNombre));
        if (archivoFoto != null) {
            mostrarFotoPerfil(archivoFoto);
        }
    } 
    
    private void mostrarFotoPerfil(File archivoFoto) {
        try {
            BufferedImage bufferImg = ImageIO.read(archivoFoto);
            Image imagen = SwingFXUtils.toFXImage(bufferImg, null);
            ivFoto.setImage(imagen);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean validarCampos() {
        String nombre = tfNombre.getText().trim();
        String apellidoPaterno = tfApellidoPaterno.getText().trim();
        String apellidoMaterno = tfApellidoMaterno.getText().trim();
        String email = tfEmail.getText().trim();
        String matricula = tfMatricula.getText().trim();
        LocalDate fechaNacimiento = dpFechaNacimiento.getValue();
        Facultad facultad = cbFacultad.getSelectionModel().getSelectedItem();
        Carrera carrera = cbCarrera.getSelectionModel().getSelectedItem();

        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty()
                || email.isEmpty() || matricula.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Campos vacíos",
                    "Por favor, completa todos los campos de texto.");
            return false;
        }

        if (fechaNacimiento == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Fecha no seleccionada",
                    "Por favor, selecciona una fecha de nacimiento.");
            return false;
        }

        if (facultad == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Facultad no seleccionada",
                    "Por favor, selecciona una facultad.");
            return false;
        }

        if (carrera == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Carrera no seleccionada",
                    "Por favor, selecciona una carrera.");
            return false;
        }

        if (archivoFoto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Foto no seleccionada",
                    "Por favor, selecciona una foto para el alumno.");
            return false;
        }

        return true;
    }
   
    private Alumno obtenerAlumnoNuevo() throws IOException{
        Alumno alumno = new Alumno();
        alumno.setNombre(tfNombre.getText());
        alumno.setApellidoPaterno(tfApellidoPaterno.getText());
        alumno.setApellidoMaterno(tfApellidoMaterno.getText());
        alumno.setMatricula(tfMatricula.getText());
        alumno.setEmail(tfEmail.getText());
        alumno.setFechaNacimiento(dpFechaNacimiento.getValue().toString());
        alumno.setIdCarrera(cbCarrera.getSelectionModel().getSelectedItem().getIdCarrera());
        alumno.setFoto(Files.readAllBytes(archivoFoto.toPath()));
        
        return alumno;
    }
    
    private Alumno obtenerAlumnoEdicion() throws IOException {
        Alumno alumno = new Alumno();
        alumno.setIdAlumno(alumnoEdicion.getIdAlumno());
        alumno.setNombre(tfNombre.getText());
        alumno.setApellidoPaterno(tfApellidoPaterno.getText());
        alumno.setApellidoMaterno(tfApellidoMaterno.getText());
        alumno.setMatricula(tfMatricula.getText());
        alumno.setEmail(tfEmail.getText());
        alumno.setFechaNacimiento(dpFechaNacimiento.getValue().toString());
        alumno.setIdCarrera(cbCarrera.getSelectionModel().getSelectedItem().getIdCarrera());
        if(archivoFoto != null) {
            byte[] foto = Files.readAllBytes(archivoFoto.toPath());
            alumno.setFoto(foto);
        } else {
            alumno.setFoto(alumnoEdicion.getFoto());
        }
        
        return alumno;
    }
    
    private void guardarAlumno(Alumno alumno) {
        try {
            ResultadoOperacion resultadoInsertar = AlumnoDAO.registrarAlumno(alumno);
            if (!resultadoInsertar.isError()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Alumno(a) registrado", "El alumno(a) " + alumno.getNombre() + " fue registrado éxito.");
                Utilidad.getEscenario(tfNombre).close();
                observador.operacionExitosa("Insertar", alumno.getNombre());
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                        "Error al regitrar", resultadoInsertar.getMensaje());
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de conexión", "Por el momento no hay conexión.");
        }
    }
    
    private void modificarAlumno(Alumno alumno){
        try {
            ResultadoOperacion resultadoModficar = AlumnoDAO.editarAlumno(alumno);
            if (!resultadoModficar.isError()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Alumno(a) modificado", "El alumno(a) " + alumno.getNombre() + " fue modificado éxito.");
                Utilidad.getEscenario(tfNombre).close();
                observador.operacionExitosa("modificar", alumno.getNombre());
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                        "Error al modificar", resultadoModficar.getMensaje());
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de conexión", "Por el momento no hay conexión.");
        }
    }
    
    private int obtenerPosicionFacultad(int idFacultad) {
        for (int i = 0; i < facultades.size(); i++){
            if (facultades.get(i).getIdFacultad() == idFacultad)
                return i;
        }
        return 0;
    }
    
    private int obtenerPosicionCarrera(int idCarrera) {
        for (int i = 0; i < carreras.size(); i++){
            if (carreras.get(i).getIdCarrera()== idCarrera)
                return i;
        }
        return 0;
    }
}