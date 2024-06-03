/**
 * 
 */
package dtos;

/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */
public class AlumnoTablaDTO {

    private int idAlumno;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String estatus;

    public AlumnoTablaDTO() {
    }

    public AlumnoTablaDTO(int idAlumno, String nombres, String apellidoPaterno, String apellidoMaterno, String estatus) {
        this.idAlumno = idAlumno;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.estatus = estatus;
    }    

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "AlumnoListadoDTO{" + "idAlumno=" + idAlumno + 
                ", nombres=" + nombres + 
                ", apellidoPaterno=" + apellidoPaterno + 
                ", apellidoMaterno=" + apellidoMaterno + 
                ", estatus=" + estatus + '}';
    }
}
