package javafxappescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.Carrera;
import javafxappescolar.modelo.pojo.Facultad;

public class CatalogoDAO {
    
    public static ArrayList<Facultad> obtenerFacultades() throws SQLException{
        ArrayList<Facultad> facultades = new ArrayList<>();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "SELECT idFacultad, nombre FROM facultad";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                facultades.add(convertirRegistroFacultad(resultado));
            }
            sentencia.close();
            resultado.close();
            conexionBD.close();
        } else {
            throw  new SQLException("Error: Sin conexión a la base de datos.");
        }
        return facultades;
    }
    
    public static ArrayList<Carrera> obtenerCarrerasPorFacultad(int idFacultad) throws SQLException{
        ArrayList<Carrera> carreras = new ArrayList<>();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "SELECT idCarrera, nombre, codigo FROM carrera WHERE idFacultad = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idFacultad);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                carreras.add(convertirRegistroCarrera(resultado));
            }
            sentencia.close();
            resultado.close();
            conexionBD.close();
        } else {
            throw  new SQLException("Error: Sin conexión a la base de datos.");
        }
        return carreras;
    }
       
    private  static Facultad convertirRegistroFacultad(ResultSet resultado) throws SQLException {
        Facultad facultad = new Facultad(resultado.getInt("idFacultad"), 
                        resultado.getString("nombre"));
        return facultad;
    }
    
    private  static Carrera convertirRegistroCarrera(ResultSet resultado) throws SQLException {
        Carrera carrera = new Carrera(resultado.getInt("idCarrera"), 
                        resultado.getString("nombre"), 
                        resultado.getString("codigo"));
        return carrera;
    }
}
