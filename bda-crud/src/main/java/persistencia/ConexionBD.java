/**
 * 
 */
package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */

public class ConexionBD implements IConexionBD {
    
    final String SERVER = "localhost";
    final String BASE_DATOS = "bdclase280524";
    private final String CADENA_CONEXION = "jdbc:mysql://" + SERVER + "/" + BASE_DATOS;
    final String USUARIO = "root";
    final String CONTRASEÑA = "13137Cas";
    
    @Override
    public Connection crearConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(CADENA_CONEXION, USUARIO, CONTRASEÑA);
        return conexion;   
    }
}