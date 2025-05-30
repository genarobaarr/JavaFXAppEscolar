package javafxappescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.ResultadoOperacion;

public class AlumnoDAO {
    
    public static ArrayList<Alumno> obtenerAlumnos() throws SQLException{
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "SELECT idAlumno, a.nombre, apellidoPaterno, " +
                    "apellidoMaterno, matricula, email, fechaNacimiento, " +
                    "c.idCarrera, c.nombre AS carrera, f.idFacultad, " +
                    "f.nombre AS facultad " +
                    "FROM alumno a " +
                    "JOIN carrera c ON a.idCarrera = c.idCarrera " +
                    "JOIN facultad f ON f.idFacultad = c.idFacultad";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                alumnos.add(convertirRegistroAlumno(resultado));
            }
            sentencia.close();
            resultado.close();
            conexionBD.close();
        } else {
            throw  new SQLException("Error: Sin conexión a la base de datos.");
        }
        return alumnos;
    }
    
    public static ResultadoOperacion registrarAlumno(Alumno alumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "INSERT INTO alumno (nombre, apellidoPaterno, " + 
                    "apellidoMaterno, matricula, email, idCarrera, fechaNacimiento, " +
                    "foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellidoPaterno());
            sentencia.setString(3, alumno.getApellidoMaterno());
            sentencia.setString(4, alumno.getMatricula());
            sentencia.setString(5, alumno.getEmail());
            sentencia.setInt(6, alumno.getIdCarrera());
            sentencia.setString(7, alumno.getFechaNacimiento());
            sentencia.setBytes(8, alumno.getFoto());
            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno(a) registrado(a) correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("Lo sentimos :( por el momento no se puede "
                        + "registrar la información del alumno(a), "
                        + "por favor inténtelo más tarde");
            }
            sentencia.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }
        return resultado;
    }
    
    public static ResultadoOperacion editarAlumno(Alumno alumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "UPDATE alumno SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, " +
                              "email = ?, idCarrera = ?, fechaNacimiento = ?, foto = ? WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellidoPaterno());
            sentencia.setString(3, alumno.getApellidoMaterno());
            sentencia.setString(4, alumno.getEmail());
            sentencia.setInt(5, alumno.getIdCarrera());
            sentencia.setString(6, alumno.getFechaNacimiento());
            sentencia.setBytes(7, alumno.getFoto());
            sentencia.setInt(8, alumno.getIdAlumno());

            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno(a) actualizado(a) correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo actualizar la información del alumno(a)");
            }
            sentencia.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }
        return resultado;
    }
    
    public static ResultadoOperacion eliminarAlumno(int idAlumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "DELETE FROM alumno WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumno);

            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno(a) eliminado(a) correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo eliminar al alumno(a)");
            }
            sentencia.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }
        return resultado;
    }

    
    
    public static byte[] obtenerFotoAlumno(int idAlumno) throws SQLException {
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "SELECT foto FROM alumno WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumno);
            ResultSet resultado = sentencia.executeQuery();

            byte[] foto = null;
            if (resultado.next()) {
                byte[] bytes = resultado.getBytes("foto");
                if (bytes != null) {
                    foto = new byte[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        foto[i] = bytes[i];
                    }
                }
            }
            resultado.close();
            sentencia.close();
            conexionBD.close();
            return foto;
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }
    }
    
    public static boolean verificarExistenciaMatricula(String matricula) throws SQLException {
        boolean existe = false;
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consulta = "SELECT COUNT(*) AS total FROM alumno WHERE matricula = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, matricula);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                existe = resultado.getInt("total") > 0;
            }
            resultado.close();
            sentencia.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }
        return existe;
    }
    
    private  static Alumno convertirRegistroAlumno(ResultSet resultado) throws SQLException {
        Alumno alumno = new Alumno(resultado.getInt("idAlumno"), 
                        resultado.getString("nombre"), resultado.getString("apellidoPaterno"), 
                        resultado.getString("apellidoMaterno"), 
                        resultado.getString("matricula"), resultado.getString("email"), 
                        resultado.getString("fechaNacimiento"), resultado.getInt("idCarrera"), 
                        resultado.getString("carrera"), resultado.getInt("idFacultad"), 
                        resultado.getString("facultad"));
        return alumno;
    }
}
