package com.jairoben.agenda.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PersonValidatorTest {

    private final PersonValidator validator = new PersonValidator();

    @Test
    void aceptaDatosCorrectos() {
        assertDoesNotThrow(() -> validator.validate(
                "Jairo", "Beneitez Baz", "jairo@ejemplo.com", LocalDate.of(2003, 5, 12)));
    }

    @Test
    void permiteFechaDeNacimientoVacia() {
        assertDoesNotThrow(() -> validator.validate("Jairo", "Beneitez", "jairo@ejemplo.com", null));
    }

    @Test
    void rechazaNombreEnBlanco() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validate("   ", "Beneitez", "jairo@ejemplo.com", null));
        assertTrue(error.fieldMessage().contains("nombre"));
    }

    @Test
    void rechazaCorreoSinArroba() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validate("Jairo", "Beneitez", "jairo.ejemplo.com", null));
        assertTrue(error.fieldMessage().contains("correo"));
    }

    @Test
    void acumulaVariosErroresEnUnMensaje() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validate("", "", "mal", null));
        String message = error.fieldMessage();
        assertTrue(message.contains("nombre"));
        assertTrue(message.contains("apellidos"));
        assertTrue(message.contains("correo"));
    }

    @Test
    void rechazaFechaFutura() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validate("Jairo", "Beneitez", "jairo@ejemplo.com", LocalDate.now().plusDays(1)));
        assertTrue(error.fieldMessage().contains("futuro"));
    }
}
