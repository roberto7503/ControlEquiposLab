package org.example.controlequipos.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.controlequipos.dao.EquipoDAO;
import org.example.controlequipos.dao.TipoEquipoDAO;
import org.example.controlequipos.model.Equipo;
import org.example.controlequipos.model.TipoEquipo;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EquipoController implements Initializable {

    // Campos de texto y selección del formulario
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtCantidad;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private ComboBox<TipoEquipo> cmbTipoEquipo;

    // Filtros de búsqueda
    @FXML
    private TextField txtBuscarNombre;
    @FXML
    private ComboBox<TipoEquipo> cmbFiltroTipo;

    // Botones
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnCargar;

    // Tabla y columnas
    @FXML
    private TableView<Equipo> tblEquipos;
    @FXML
    private TableColumn<Equipo, Long> colId;
    @FXML
    private TableColumn<Equipo, String> colCodigo;
    @FXML
    private TableColumn<Equipo, String> colNombre;
    @FXML
    private TableColumn<Equipo, String> colMarca;
    @FXML
    private TableColumn<Equipo, String> colEstado;
    @FXML
    private TableColumn<Equipo, Integer> colCantidad;
    @FXML
    private TableColumn<Equipo, String> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private final ObservableList<Equipo> listaEquipos = FXCollections.observableArrayList();
    private final ObservableList<TipoEquipo> listaTipos = FXCollections.observableArrayList();
    private final ObservableList<String> listaEstados = FXCollections.observableArrayList(
            "Disponible", "En mantenimiento", "Fuera de servicio"
    );

    private Long idSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnasTabla();
        configurarComboBoxes();
        configurarSeleccionTabla();

        // Cargar datos iniciales
        cargarTiposEquipo();
        cargarEquipos();
    }

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colMarca.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getMarca() != null ? cell.getValue().getMarca() : ""
        ));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado()));
        colCantidad.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());

        colTipo.setCellValueFactory(cell -> {
            TipoEquipo tipo = cell.getValue().getTipoEquipo();
            return new SimpleStringProperty(tipo != null ? tipo.getNombre() : "Sin tipo");
        });

        tblEquipos.setItems(listaEquipos);
    }

    private void configurarComboBoxes() {
        cmbEstado.setItems(listaEstados);

        StringConverter<TipoEquipo> converter = new StringConverter<>() {
            @Override
            public String toString(TipoEquipo tipo) {
                return tipo != null ? tipo.getNombre() : "";
            }

            @Override
            public TipoEquipo fromString(String string) {
                return null;
            }
        };

        cmbTipoEquipo.setItems(listaTipos);
        cmbTipoEquipo.setConverter(converter);

        cmbFiltroTipo.setConverter(converter);
    }

    private void configurarSeleccionTabla() {
        tblEquipos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevoEquipo) -> {
            if (nuevoEquipo != null) {
                idSeleccionado = nuevoEquipo.getId();
                txtCodigo.setText(nuevoEquipo.getCodigo());
                txtNombre.setText(nuevoEquipo.getNombre());
                txtMarca.setText(nuevoEquipo.getMarca() != null ? nuevoEquipo.getMarca() : "");
                txtCantidad.setText(String.valueOf(nuevoEquipo.getCantidad()));
                cmbEstado.setValue(nuevoEquipo.getEstado());

                if (nuevoEquipo.getTipoEquipo() != null) {
                    for (TipoEquipo tipo : listaTipos) {
                        if (tipo.getId().equals(nuevoEquipo.getTipoEquipo().getId())) {
                            cmbTipoEquipo.setValue(tipo);
                            break;
                        }
                    }
                } else {
                    cmbTipoEquipo.setValue(null);
                }
            }
        });
    }

    public void cargarTiposEquipo() {
        try {
            List<TipoEquipo> tipos = tipoEquipoDAO.listar();
            listaTipos.setAll(tipos);

            ObservableList<TipoEquipo> filtroTipos = FXCollections.observableArrayList();
            filtroTipos.add(null); // Opción para "Todos los tipos"
            filtroTipos.addAll(tipos);
            cmbFiltroTipo.setItems(filtroTipos);
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los tipos de equipo: " + ex.getMessage());
        }
    }

    @FXML
    public void cargarEquipos() {
        try {
            List<Equipo> equipos = equipoDAO.listar();
            listaEquipos.setAll(equipos);
            txtBuscarNombre.clear();
            cmbFiltroTipo.setValue(null);
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar equipos: " + ex.getMessage());
        }
    }

    @FXML
    public void buscarYFiltrarEquipos(ActionEvent event) {
        try {
            String nombre = txtBuscarNombre.getText();
            TipoEquipo tipo = cmbFiltroTipo.getValue();

            List<Equipo> resultados = equipoDAO.buscarYFiltrar(nombre, tipo);
            listaEquipos.setAll(resultados);

            if (resultados.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Búsqueda", "No se encontraron equipos con los criterios especificados.");
            }
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al buscar equipos: " + ex.getMessage());
        }
    }

    @FXML
    public void guardarEquipo(ActionEvent event) {
        try {
            idSeleccionado = null;
            Equipo nuevoEquipo = obtenerEquipoFormulario();

            equipoDAO.guardar(nuevoEquipo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo guardado correctamente.");
            limpiarCampos();
            cargarEquipos();
        } catch (IllegalArgumentException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el equipo: " + ex.getMessage());
        }
    }

    @FXML
    public void modificarEquipo(ActionEvent event) {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un equipo de la tabla para modificar.");
            return;
        }

        try {
            Equipo equipoModificado = obtenerEquipoFormulario();
            boolean exito = equipoDAO.modificar(equipoModificado);

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo modificado correctamente.");
                limpiarCampos();
                cargarEquipos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el equipo en la base de datos.");
            }
        } catch (IllegalArgumentException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo modificar el equipo: " + ex.getMessage());
        }
    }

    @FXML
    public void eliminarEquipo(ActionEvent event) {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un equipo de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar este equipo?", ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            try {
                boolean exito = equipoDAO.eliminar(idSeleccionado);
                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo eliminado correctamente.");
                    limpiarCampos();
                    cargarEquipos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo encontrar el equipo para eliminar.");
                }
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    @FXML
    public void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbEstado.setValue(null);
        cmbTipoEquipo.setValue(null);
        idSeleccionado = null;
        tblEquipos.getSelectionModel().clearSelection();
    }

    private Equipo obtenerEquipoFormulario() {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String marca = txtMarca.getText() != null ? txtMarca.getText().trim() : "";

        if (codigo.isEmpty() || nombre.isEmpty()) {
            throw new IllegalArgumentException("Completa el código y el nombre del equipo.");
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero válido.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa (mínimo 0).");
        }

        String estado = cmbEstado.getValue();
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("Debes seleccionar un estado válido.");
        }
        List<String> permitidos = Arrays.asList("Disponible", "En mantenimiento", "Fuera de servicio");
        if (!permitidos.contains(estado)) {
            throw new IllegalArgumentException("El estado seleccionado no es válido.");
        }

        TipoEquipo tipo = cmbTipoEquipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Debes seleccionar un tipo de equipo.");
        }

        return new Equipo(idSeleccionado, codigo, nombre, marca, estado, cantidad, tipo);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}