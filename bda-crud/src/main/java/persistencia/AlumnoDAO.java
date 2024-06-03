/**
 * 
 */
package persistencia;

import entidad.AlumnoEntidad;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */
public class AlumnoDAO implements IAlumnoDAO {
    private IConexionBD conexionBD;

    public AlumnoDAO(IConexionBD conexionBD){
        this.conexionBD = conexionBD;
    }

    @Override
    public List<AlumnoEntidad> buscarAlumnosTabla() throws PersistenciaException {
        try {
            List<AlumnoEntidad> alumnosLista = null;
            Connection conexion = this.conexionBD.crearConexion();
            String codigoSQL = "SELECT idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo FROM alumnos";
            Statement comandoSQL = conexion.createStatement();
            ResultSet resultado = comandoSQL.executeQuery(codigoSQL);
            while (resultado.next()){
                if(alumnosLista == null){
                alumnosLista = new ArrayList<>();
            }
            AlumnoEntidad alumno = this.convertirAEntidad(resultado);
            alumnosLista.add(alumno);
            }
            conexion.close();
            return alumnosLista;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Ocurrió un error al leer la base de datos, inténtelo de nuevo.");
        }
        
    }
        private AlumnoEntidad convertirAEntidad(ResultSet resultado) throws SQLException {
            int id = resultado.getInt("idAlumno");
            String nombre = resultado.getString("nombres");
            String paterno = resultado.getString("apellidoPaterno");
            String materno = resultado.getString("apellidoMaterno");
            boolean eliminado = resultado.getBoolean("eliminado");
            boolean activo = resultado.getBoolean("activo");
            return new AlumnoEntidad(id,nombre,paterno,materno,eliminado,activo);
        }
}
