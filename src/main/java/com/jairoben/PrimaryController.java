package com.jairoben;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador FXML de la vista principal de la aplicación.
 * Gestiona la tabla de personas y las operaciones de alta, borrado
 * de la selección y restauración de los datos iniciales.
 */
public class PrimaryController {

    /**
     * Bundle de textos inyectado por el {@link javafx.fxml.FXMLLoader}
     * (clave {@code resources}). Contiene todos los mensajes de la aplicación.
     */
    @FXML
    private ResourceBundle resources;

    /**
     * Botón que da de alta una nueva persona a partir de los datos del formulario.
     */
    @FXML
    private Button btAdd;

    /**
     * Botón que elimina de la tabla las filas seleccionadas.
     */
    @FXML
    private Button btDeleteSelected;

    /**
     * Botón que repone la tabla con la copia de seguridad de los datos iniciales.
     */
    @FXML
    private Button btRestore;

    /**
     * Columna que muestra la fecha de nacimiento de la persona.
     */
    @FXML
    private TableColumn<Person, LocalDate> colBirthDate;

    /**
     * Columna que muestra el nombre de la persona.
     */
    @FXML
    private TableColumn<Person, String> colFirstName;

    /**
     * Columna que muestra el identificador único de la persona.
     */
    @FXML
    private TableColumn<Person, Integer> colId;

    /**
     * Columna que muestra los apellidos de la persona.
     */
    @FXML
    private TableColumn<Person, String> colLastName;

    /**
     * Selector de fecha usado para indicar la fecha de nacimiento en el alta.
     */
    @FXML
    private DatePicker dpBirth;

    /**
     * Campo de texto del formulario donde se introduce el nombre.
     */
    @FXML
    private TextField tfFirstName;

    /**
     * Campo de texto del formulario donde se introducen los apellidos.
     */
    @FXML
    private TextField tfLastName;

    /**
     * Tabla que muestra la lista de personas disponibles.
     */
    @FXML
    private TableView<Person> tvPeople;

    /**
     * Lista observable de personas mostrada en la tabla, la que reciben
     * las altas y los borrados.
     */
    private final ObservableList<Person> datos = FXCollections.observableArrayList();

    /**
     * Copia de seguridad de los datos de partida, usada por
     * {@link #restore(ActionEvent)} para restaurar la tabla.
     */
    private final ObservableList<Person> datosIniciales = FXCollections.observableArrayList();

    /**
     * Valida los campos del formulario y, si todos están rellenos y la fecha
     * no es futura, añade una nueva {@link Person} a la tabla y limpia el
     * formulario. Si algún campo obligatorio está vacío o la fecha de
     * nacimiento es posterior a hoy muestra una advertencia y no realiza el alta.
     *
     * @param event el evento de acción disparado por el botón de alta.
     */
    @FXML
    void add(ActionEvent event) {
        String nombre = tfFirstName.getText().trim();
        String apellidos = tfLastName.getText().trim();
        LocalDate fecha = dpBirth.getValue();

        if (nombre.isEmpty() || apellidos.isEmpty() || fecha == null) {
            new Alert(Alert.AlertType.WARNING, resources.getString("aviso.campos.obligatorios"))
                    .showAndWait();
            return;
        }

        if (fecha.isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, resources.getString("aviso.fecha.futura"))
                    .showAndWait();
            return;
        }

        datos.add(new Person(nombre, apellidos, fecha));
        tfFirstName.clear();
        tfLastName.clear();
        dpBirth.setValue(null);
        tfFirstName.requestFocus();
    }

    /**
     * Elimina de la tabla las personas actualmente seleccionadas.
     * La selección se copia antes a un {@link ArrayList} independiente para
     * evitar una {@link java.util.ConcurrentModificationException} al quitar
     * elementos de la lista observable. Si no hay selección muestra un aviso.
     *
     * @param event el evento de acción disparado por el botón de borrado.
     */
    @FXML
    void deleteSelected(ActionEvent event) {
        List<Person> seleccion = new ArrayList<>(tvPeople.getSelectionModel().getSelectedItems());

        if (seleccion.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, resources.getString("aviso.seleccion.vacia")).showAndWait();
            return;
        }

        datos.removeAll(seleccion);
    }

    /**
     * Restaura el contenido de la tabla a partir de la copia de seguridad
     * {@link #datosIniciales}, deshaciendo las altas y los borrados realizados.
     *
     * @param event el evento de acción disparado por el botón de restauración.
     */
    @FXML
    void restore(ActionEvent event) {
        datos.setAll(datosIniciales);
    }

    /**
     * Inicializa la vista una vez que {@link javafx.fxml.FXMLLoader} ha
     * cargado el FXML (lo invoca automáticamente FXMLLoader).
     * Enlaza cada columna con su propiedad de {@link Person} mediante
     * {@code PropertyValueFactory}, activa la selección múltiple en la tabla,
     * deshabilita en el calendario los días posteriores a hoy y carga cinco
     * personas de ejemplo en la tabla.
     */
    @FXML
    void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        tvPeople.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        dpBirth.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isAfter(LocalDate.now()));
            }
        });

        datosIniciales.addAll(
                new Person("Ashwin", "Sharan", LocalDate.of(2012, 10, 11)),
                new Person("Advik", "Sharan", LocalDate.of(2012, 10, 11)),
                new Person("Layne", "Estes", LocalDate.of(2011, 12, 16)),
                new Person("Mason", "Boyd", LocalDate.of(2003, 4, 20)),
                new Person("Babalu", "Sharan", LocalDate.of(1980, 1, 10)));

        datos.addAll(datosIniciales);
        tvPeople.setItems(datos);
    }

}
