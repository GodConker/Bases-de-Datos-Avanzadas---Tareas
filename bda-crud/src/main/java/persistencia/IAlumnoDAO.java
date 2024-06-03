/**
 * 
 */
package persistencia;

import entidad.AlumnoEntidad;
import java.util.List;

/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */
public interface IAlumnoDAO {
    public List<AlumnoEntidad> buscarAlumnosTabla() throws PersistenciaException;
    
//    public AlumnoEntidad buscarPorIdAlumno(int id) throws PersistenciaException;
    
}
