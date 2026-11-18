package com.jairoben.agenda.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonServiceTest {

    private PersonService service;

    @BeforeEach
    void setUp() {
        service = new PersonService(new InMemoryPersonRepository(), new PersonValidator());
    }

    @Test
    void creaUnContactoYLeAsignaId() {
        Person created = service.create("Jairo", "Beneitez", "jairo@ejemplo.com", LocalDate.of(2003, 5, 12));

        assertTrue(created.id() > 0);
        assertEquals(1, service.count());
    }

    @Test
    void normalizaElCorreoAminusculasSinEspacios() {
        Person created = service.create("Jairo", "Beneitez", "  Jairo@Ejemplo.COM ", null);

        assertEquals("jairo@ejemplo.com", created.email());
    }

    @Test
    void impideDosContactosConElMismoCorreo() {
        service.create("Jairo", "Beneitez", "jairo@ejemplo.com", null);

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.create("Otro", "Contacto", "JAIRO@ejemplo.com", null));
        assertTrue(error.fieldMessage().contains("Ya existe"));
    }

    @Test
    void actualizaLosDatosManteniendoElId() {
        Person created = service.create("Jairo", "Beneitez", "jairo@ejemplo.com", null);

        Person updated = service.update(created.id(), "Jairo", "Beneitez Baz", "jairo@ejemplo.com", null);

        assertEquals(created.id(), updated.id());
        assertEquals("Beneitez Baz", service.findAll().get(0).lastName());
    }

    @Test
    void noPermiteActualizarHaciaUnCorreoOcupadoPorOtro() {
        service.create("Ana", "Ruiz", "ana@ejemplo.com", null);
        Person jairo = service.create("Jairo", "Beneitez", "jairo@ejemplo.com", null);

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.update(jairo.id(), "Jairo", "Beneitez", "ana@ejemplo.com", null));
        assertTrue(error.fieldMessage().contains("Ana Ruiz"));
    }

    @Test
    void borrarUnContactoInexistenteAvisaAlUsuario() {
        assertThrows(ValidationException.class, () -> service.delete(99L));
    }

    @Test
    void laBusquedaVaciaDevuelveTodo() {
        service.create("Ana", "Ruiz", "ana@ejemplo.com", null);
        service.create("Jairo", "Beneitez", "jairo@ejemplo.com", null);

        List<Person> result = service.search("   ");

        assertEquals(2, result.size());
    }

    @Test
    void laBusquedaFiltraPorCorreoAunqueElServicioRecibaMayusculas() {
        service.create("Ana", "Ruiz", "ana@ejemplo.com", null);
        service.create("Jairo", "Beneitez", "jairo@ejemplo.com", null);

        List<Person> result = service.search("ANA@");

        assertEquals(1, result.size());
        assertEquals("Ruiz", result.get(0).lastName());
    }
}
