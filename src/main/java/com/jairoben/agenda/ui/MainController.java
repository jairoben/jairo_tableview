package com.jairoben.agenda.ui;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jairoben.agenda.domain.Person;
import com.jairoben.agenda.domain.PersonService;
import com.jairoben.agenda.domain.ValidationException;
import com.jairoben.agenda.infrastructure.RepositoryException;

/** Controlador de la vista principal: conecta la tabla, el formulario y el servicio. */
public final class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthPicker;
    @FXML private TableView<Person> table;
    @FXML private TableColumn<Person, String> colFirstName;
    @FXML private TableColumn<Person, String> colLastName;
    @FXML private TableColumn<Person, String> colEmail;
    @FXML private TableColumn<Person, String> colBirth;
    @FXML private Label countLabel;
    @FXML private Label statusLabel;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private PersonService service;

    @FXML
    private void initialize() {
        colFirstName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().firstName()));
        colLastName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().lastName()));
        colEmail.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().email()));
        colBirth.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().birthDate() == null ? "—" : cell.getValue().birthDate().toString()));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, selected) -> fillForm(selected));

        searchField.textProperty().addListener((observable, previous, current) -> refresh());

        updateButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
    }

    /** Se llama desde {@code AgendaApp} cuando el servicio ya está listo. */
    public void init(PersonService service) {
        this.service = service;
        refresh();
        setStatus("Listo. " + service.count() + " contactos en la agenda.", false);
    }

    @FXML
    private void onSave() {
        try {
            Person created = service.create(
                    firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), birthPicker.getValue());
            clearForm();
            refresh();
            setStatus("Contacto guardado: " + created.fullName(), false);
        } catch (ValidationException e) {
            setStatus(oneLine(e.fieldMessage()), true);
        } catch (RepositoryException e) {
            log.error("Fallo al guardar el contacto", e);
            setStatus("No se pudo guardar en la base de datos.", true);
        }
    }

    @FXML
    private void onUpdate() {
        Person selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Selecciona un contacto en la tabla para modificarlo.", true);
            return;
        }
        try {
            service.update(selected.id(), firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), birthPicker.getValue());
            refresh();
            setStatus("Contacto actualizado.", false);
        } catch (ValidationException e) {
            setStatus(oneLine(e.fieldMessage()), true);
        } catch (RepositoryException e) {
            log.error("Fallo al actualizar el contacto {}", selected.id(), e);
            setStatus("No se pudo actualizar en la base de datos.", true);
        }
    }

    @FXML
    private void onDelete() {
        Person selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar a " + selected.fullName() + " de la agenda?", ButtonType.CANCEL, ButtonType.OK);
        confirmation.setHeaderText(null);
        confirmation.setTitle("Borrar contacto");
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            service.delete(selected.id());
            clearForm();
            refresh();
            setStatus("Contacto borrado.", false);
        } catch (ValidationException e) {
            setStatus(oneLine(e.fieldMessage()), true);
        } catch (RepositoryException e) {
            log.error("Fallo al borrar el contacto {}", selected.id(), e);
            setStatus("No se pudo borrar de la base de datos.", true);
        }
    }

    @FXML
    private void onClear() {
        clearForm();
        setStatus("Formulario limpio.", false);
    }

    private void refresh() {
        if (service == null) {
            return;
        }
        try {
            List<Person> rows = service.search(searchField.getText());
            table.setItems(FXCollections.observableArrayList(rows));
            countLabel.setText(rows.size() == 1 ? "1 contacto" : rows.size() + " contactos");
        } catch (RepositoryException e) {
            log.error("Fallo al refrescar la tabla", e);
            setStatus("No se pudo leer la agenda.", true);
        }
    }

    private void fillForm(Person person) {
        if (person == null) {
            return;
        }
        firstNameField.setText(person.firstName());
        lastNameField.setText(person.lastName());
        emailField.setText(person.email());
        LocalDate birth = person.birthDate();
        birthPicker.setValue(birth);
        setStatus("Editando: " + person.fullName(), false);
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        birthPicker.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().remove("status-error");
        if (error) {
            statusLabel.getStyleClass().add("status-error");
        }
    }

    private static String oneLine(String text) {
        return text.replace("\n", " ");
    }
}
