/**
 *
 */
package presentacion;

import dtos.AlumnoTablaDTO;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import negocio.AlumnoNegocio;
import negocio.IAlumnoNegocio;
import negocio.NegocioException;
import persistencia.AlumnoDAO;
import persistencia.ConexionBD;
import persistencia.IAlumnoDAO;
import persistencia.IConexionBD;
import utilerias.JButtonCellEditor;
import utilerias.JButtonRenderer;

/**
 *
 * @author Daniel Alejandro Castro Félix - 235294.
 */
public class FrmCRUD extends javax.swing.JFrame {

    private int pagina = 1;
    private final int LIMITE = 2;
    private IAlumnoNegocio alumnoNegocio;

    /**
     * Creates new form FrmCRUD
     *
     * @param alumnoNegocio
     */
    public FrmCRUD(IAlumnoNegocio alumnoNegocio) {
        initComponents();

        // Configurar los botones como no visibles al inicio
        btnCancelar.setVisible(false);
        btnRegistrarEditar.setVisible(false);

        this.alumnoNegocio = alumnoNegocio;
        this.cargarMetodosIniciales();
    }

    private void cargarMetodosIniciales() {
        this.cargarConfiguracionInicialPantalla();
        this.cargarConfiguracionInicialTablaAlumnos();
        this.cargarAlumnosEnTabla();
    }

    private void cargarConfiguracionInicialPantalla() {
        // Obtener las dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calcular las dimensiones para 3/4 de la pantalla
        int width = (int) (screenSize.width * 0.75);
        int height = (int) (screenSize.height * 0.75);

        // Establecer el tamaño del frame
        this.setSize(width, height);

        // Centrar el frame en la pantalla
        this.setLocationRelativeTo(null);
    }

    private void cargarConfiguracionInicialTablaAlumnos() {
        ActionListener onEditarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para editar un alumno
                editar();
            }
        };
        int indiceColumnaEditar = 5;
        TableColumnModel modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellRenderer(new JButtonRenderer("Editar"));
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellEditor(new JButtonCellEditor("Editar",
                        onEditarClickListener));

        ActionListener onEliminarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para eliminar un alumno
                eliminar();
            }
        };
        int indiceColumnaEliminar = 6;
        modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellRenderer(new JButtonRenderer("Eliminar"));
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellEditor(new JButtonCellEditor("Eliminar",
                        onEliminarClickListener));
    }

    private int getIdSeleccionadoTablaAlumnos() {
        int indiceFilaSeleccionada = this.tblAlumnos.getSelectedRow();
        if (indiceFilaSeleccionada != -1) {
            DefaultTableModel modelo = (DefaultTableModel) this.tblAlumnos.getModel();
            int indiceColumnaId = 0;
            int idSocioSeleccionado = (int) modelo.getValueAt(indiceFilaSeleccionada, indiceColumnaId);
            return idSocioSeleccionado;
        } else {
            return -1; // Indica que no hay ninguna fila seleccionada
        }
    }

    public static boolean esNombreValido(String nombre) {
        // Expresión regular para validar nombres
        String regex = "^[A-Za-záéíóúÁÉÍÓÚñÑüÜ\\s]+$"; // Permite letras (mayúsculas y minúsculas), espacios y caracteres acentuados

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nombre);

        return matcher.matches();
    }

    private void registrarAlumno() {
        try {
            // Establecer la conexión a la base de datos
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/bdclase280524", "root", "13137Cas");

            // Validar que los campos obligatorios no estén vacíos
            String nombres = txtNombres.getText().trim();
            String apellidoPaterno = txtApellidoPaterno.getText().trim();

            if (nombres.isEmpty() || apellidoPaterno.isEmpty()) {
                // Mostrar un mensaje de error si faltan datos obligatorios
                JOptionPane.showMessageDialog(this, "Por favor, ingresa la información obligatoria correctamente solicitada en este formulario.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir del método sin registrar el alumno
            }

            // Validar que el nombre sea válido (solo letras, espacios y caracteres acentuados)
            if (!esNombreValido(nombres)) {
                JOptionPane.showMessageDialog(this, "El nombre ingresado no es válido. Por favor, verifica que solo contenga letras y espacios.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir del método sin registrar el alumno
            }

            // Sentencia SQL para insertar un nuevo alumno en la tabla 'alumnos'
            String sentenciaSql = "INSERT INTO alumnos (nombres, apellidoPaterno, apellidoMaterno, eliminado, activo) VALUES (?, ?, ?, ?, ?);";

            // Preparar la sentencia SQL, permitiendo obtener las claves generadas automáticamente
            PreparedStatement preparedStatement = conexion.prepareStatement(sentenciaSql, Statement.RETURN_GENERATED_KEYS);

            // Establecer los valores para los parámetros de la sentencia SQL
            preparedStatement.setString(1, nombres);
            preparedStatement.setString(2, apellidoPaterno);
            preparedStatement.setString(3, txtApellidoMaterno.getText()); // Puede estar vacío
            preparedStatement.setBoolean(4, false); // Siempre falso al registrar un nuevo alumno
            preparedStatement.setBoolean(5, true); // Siempre activo al registrar un nuevo alumno

            // Ejecutar la sentencia SQL de inserción
            preparedStatement.executeUpdate();

            // Obtener las claves generadas automáticamente (por ejemplo, el ID del nuevo registro)
            ResultSet resultado = preparedStatement.getGeneratedKeys();
            while (resultado.next()) {
                // Imprimir el ID generado para el nuevo registro
                System.out.println("Alumno registrado con ID: " + resultado.getInt(1));
            }

            // Cerrar la conexión
            conexion.close();
        } catch (SQLException ex) {
            // Capturar y manejar cualquier excepción SQL que ocurra
            System.out.println("Ocurrió un error: " + ex.getMessage());
        }
        cargarAlumnosEnTabla();
    }

    private void editar() {
        int id = getIdSeleccionadoTablaAlumnos();

        if (id == -1) {
            // Mostrar mensaje de error si no hay ninguna fila seleccionada
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un alumno de la tabla antes de editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtén los valores de los campos desde FrmCRUD
        DefaultTableModel modelo = (DefaultTableModel) this.tblAlumnos.getModel();
        int indiceFilaSeleccionada = this.tblAlumnos.getSelectedRow();
        String nombres = (String) modelo.getValueAt(indiceFilaSeleccionada, 1);
        String apellidoPaterno = (String) modelo.getValueAt(indiceFilaSeleccionada, 2);
        String apellidoMaterno = (String) modelo.getValueAt(indiceFilaSeleccionada, 3);

        // Crea una instancia de FrmEditarAlumno y pásale los valores y la referencia a FrmCRUD
        FrmEditarAlumno frmEditar = new FrmEditarAlumno(this, id, nombres, apellidoPaterno, apellidoMaterno);

        // Muestra el frame FrmEditarAlumno
        frmEditar.setVisible(true);
    }

    private void eliminar() {
        int indiceFilaSeleccionada = this.tblAlumnos.getSelectedRow();

        if (indiceFilaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un alumno para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) this.tblAlumnos.getModel();
        int idAlumno = (int) modelo.getValueAt(indiceFilaSeleccionada, 0);

        // Mostrar confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este alumno?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Si el usuario confirma, eliminar el registro
            try {
                // Establecer la conexión a la base de datos
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/bdclase280524", "root", "13137Cas");

                // Sentencia SQL para eliminar el alumno
                String sentenciaSql = "DELETE FROM alumnos WHERE idAlumno = ?";
                PreparedStatement preparedStatement = conexion.prepareStatement(sentenciaSql);
                preparedStatement.setInt(1, idAlumno);

                // Ejecutar la sentencia SQL
                preparedStatement.executeUpdate();

                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(this, "Alumno eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Cerrar la conexión
                conexion.close();

                // Recargar la tabla
                cargarAlumnosEnTabla();
            } catch (SQLException ex) {
                // Manejar la excepción
                System.out.println("Ocurrió un error: " + ex.getMessage());
            }
        }
    }

    private void llenarTablaAlumnos(List<AlumnoTablaDTO> alumnosLista) {
        DefaultTableModel modeloTabla = (DefaultTableModel) this.tblAlumnos.getModel();

        if (modeloTabla.getRowCount() > 0) {
            for (int i = modeloTabla.getRowCount() - 1; i > -1; i--) {
                modeloTabla.removeRow(i);
            }
        }

        if (alumnosLista != null) {
            alumnosLista.forEach(row -> {
                Object[] fila = new Object[5];
                fila[0] = row.getIdAlumno();
                fila[1] = row.getNombres();
                fila[2] = row.getApellidoPaterno();
                fila[3] = row.getApellidoMaterno();
                fila[4] = row.getEstatus();

                modeloTabla.addRow(fila);
            });
        }
    }

    void cargarAlumnosEnTabla() {
        try {
            List<AlumnoTablaDTO> alumnos = alumnoNegocio.buscarAlumnosTabla();
            int inicio = (pagina - 1) * LIMITE;
            int fin = Math.min(inicio + LIMITE, alumnos.size());
            List<AlumnoTablaDTO> alumnosPaginados = alumnos.subList(inicio, fin);
            // Ahora, actualiza la tabla con la lista de alumnosPaginados
            this.llenarTablaAlumnos(alumnosPaginados); // Utiliza la lista alumnosPaginados aquí
        } catch (NegocioException ex) {
//            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblNombres = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        lblApellidoPaterno = new javax.swing.JLabel();
        txtApellidoPaterno = new javax.swing.JTextField();
        lblApellidoMaterno = new javax.swing.JLabel();
        txtApellidoMaterno = new javax.swing.JTextField();
        chbActivo = new javax.swing.JCheckBox();
        btnRegistrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlumnos = new javax.swing.JTable();
        btnAtras = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        lblPagina = new javax.swing.JLabel();
        lblObligatorioNombres = new javax.swing.JLabel();
        lblObligatorioNombres1 = new javax.swing.JLabel();
        lblObligatorioNombres2 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnRegistrarEditar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administración de Alumnos");

        lblTitulo.setText("Administración de Alumnos");

        lblNombres.setText("Nombres");

        lblApellidoPaterno.setText("Apellido Paterno");

        lblApellidoMaterno.setText("Apellido Materno");

        chbActivo.setText("Activo");

        btnRegistrar.setText("Nuevo Registro");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        tblAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombres", "A. Paterno", "A. Materno", "Estatus", "Editar", "Eliminar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAlumnos);

        btnAtras.setText("Atrás");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        lblPagina.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPagina.setText("Página 1");
        lblPagina.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblObligatorioNombres.setForeground(new java.awt.Color(255, 0, 0));
        lblObligatorioNombres.setText("Obligatorio *");

        lblObligatorioNombres1.setForeground(new java.awt.Color(255, 0, 0));
        lblObligatorioNombres1.setText("Obligatorio *");

        lblObligatorioNombres2.setForeground(new java.awt.Color(0, 204, 204));
        lblObligatorioNombres2.setText("Opcional *");

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnRegistrarEditar.setText("Registrar");
        btnRegistrarEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblNombres)
                                                .addGap(8, 8, 8)
                                                .addComponent(lblObligatorioNombres)))
                                        .addGap(18, 18, 18)
                                        .addComponent(lblApellidoPaterno)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblObligatorioNombres1)
                                        .addGap(92, 92, 92)
                                        .addComponent(lblApellidoMaterno)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblObligatorioNombres2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(258, 258, 258)
                                        .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btnCancelar)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnRegistrarEditar)
                                                .addGap(49, 49, 49)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnRegistrar)
                                            .addComponent(chbActivo)))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblTitulo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAtras)
                        .addGap(347, 347, 347)
                        .addComponent(lblPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(336, 336, 336)
                        .addComponent(btnSiguiente)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombres)
                    .addComponent(lblApellidoPaterno)
                    .addComponent(lblApellidoMaterno)
                    .addComponent(lblObligatorioNombres)
                    .addComponent(lblObligatorioNombres1)
                    .addComponent(lblObligatorioNombres2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chbActivo))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnCancelar)
                    .addComponent(btnRegistrarEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAtras)
                    .addComponent(btnSiguiente)
                    .addComponent(lblPagina, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:

        // Después de registrar el alumno, muestra los botones
        btnCancelar.setVisible(true);
        btnRegistrarEditar.setVisible(true);
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        // TODO add your handling code here:
        if (pagina > 1) {
            pagina--;
            lblPagina.setText("Página " + pagina); // Actualiza el texto del JLabel
            cargarAlumnosEnTabla();
        }
    }//GEN-LAST:event_btnAtrasActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        // TODO add your handling code here:
        try {
            List<AlumnoTablaDTO> alumnos = alumnoNegocio.buscarAlumnosTabla();
            int totalPaginas = (int) Math.ceil((double) alumnos.size() / LIMITE);
            if (pagina < totalPaginas) {
                pagina++;
                cargarAlumnosEnTabla();
                lblPagina.setText("Página " + pagina); // Actualiza el texto del JLabel
            }
        } catch (NegocioException ex) {
            // Manejo de excepciones
            System.out.println(ex.getMessage());
        }
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnRegistrarEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarEditarActionPerformed
        // TODO add your handling code here:
        registrarAlumno();
    }//GEN-LAST:event_btnRegistrarEditarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        // Limpiar los campos de texto
        txtNombres.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");

        // Ocultar los botones btnCancelar y btnRegistrarEditar
        btnCancelar.setVisible(false);
        btnRegistrarEditar.setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            // Crea una instancia de ConexionBD (o la implementación real que estés utilizando)
            IConexionBD conexionBD = new ConexionBD();

            // Crea una instancia de AlumnoDAO pasando la conexión como argumento
            IAlumnoDAO alumnoDAO = new AlumnoDAO(conexionBD);

            // Crea una instancia de AlumnoNegocio pasando el DAO como argumento
            IAlumnoNegocio alumnoNegocio = new AlumnoNegocio(alumnoDAO);

            // Crea una instancia de FrmCRUD pasando el negocio como argumento
            FrmCRUD frmCRUD = new FrmCRUD(alumnoNegocio);

            // Hacer visible el JFrame
            frmCRUD.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnRegistrarEditar;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JCheckBox chbActivo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellidoMaterno;
    private javax.swing.JLabel lblApellidoPaterno;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblObligatorioNombres;
    private javax.swing.JLabel lblObligatorioNombres1;
    private javax.swing.JLabel lblObligatorioNombres2;
    private javax.swing.JLabel lblPagina;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblAlumnos;
    private javax.swing.JTextField txtApellidoMaterno;
    private javax.swing.JTextField txtApellidoPaterno;
    private javax.swing.JTextField txtNombres;
    // End of variables declaration//GEN-END:variables
}
