/**
 * 
 */
package negocio;

import dtos.AlumnoTablaDTO;
import java.util.List;

/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */
public interface IAlumnoNegocio {
    public List<AlumnoTablaDTO> buscarAlumnosTabla() throws NegocioException;
    
//    public AlumnoLecturaDTO insertar(GuardarAlumnoDTO alumno) throws NegocioException;
//    
//    public AlumnoLecturaDTO obtenerPorId(int id) throws NegocioException;
//    
//    public EditarAlumnoDTO editar(EditarAlumno alumno) throws NegocioException;
}
