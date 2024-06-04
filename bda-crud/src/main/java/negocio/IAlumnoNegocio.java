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

    public void registrarAlumno(String nombres, String apellidoPaterno, String apellidoMaterno) throws NegocioException;

    public void editarAlumno(int idAlumno, String nombres, String apellidoPaterno, String apellidoMaterno) throws NegocioException;
    
    public void eliminarAlumno(int idAlumno) throws NegocioException;
}
