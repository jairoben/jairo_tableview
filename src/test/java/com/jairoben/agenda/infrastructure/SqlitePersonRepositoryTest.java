package com.jairoben.agenda.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.jairoben.agenda.domain.Person;

class SqlitePersonRepositoryTest {

    @TempDir
    Path tempDir;

    private String jdbcUrl;
    private SqlitePersonRepository repository;

    @BeforeEach
    void setUp() {
        jdbcUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        ConnectionFactory connectionFactory = new ConnectionFactory(jdbcUrl);
        new SchemaMigrator(connectionFactory).migrate();
        repository = new SqlitePersonRepository(connectionFactory);
    }

    @Test
    void guardaYDevuelveElContactoSinId() {
        Person saved = repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));

        assertTrue(saved.id() > 0);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void conservaLaFechaDeNacimiento() {
        LocalDate birth = LocalDate.of(2003, 5, 12);
        repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", birth));

        Person found = repository.findAll().get(0);

        assertEquals(birth, found.birthDate());
    }

    @Test
    void ordenaAlfabeticamentePorApellidos() {
        repository.insert(Person.newPerson("Ana", "Zapata", "ana@ejemplo.com", null));
        repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));

        List<Person> people = repository.findAll();

        assertEquals("Jairo", people.get(0).firstName());
    }

    @Test
    void buscaPorNombreApellidoOCorreo() {
        repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));
        repository.insert(Person.newPerson("Ana", "Ruiz", "ana@ejemplo.com", null));

        assertEquals(1, repository.search("beneitez").size());
        assertEquals(1, repository.search("ANA@").size());
        assertEquals(2, repository.search("ejemplo").size());
    }

    @Test
    void actualizaYLeeElContactoPorId() {
        Person saved = repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));
        Person updated = new Person(saved.id(), "Jairo", "Beneitez Baz", "jairo@ejemplo.com", null);

        repository.update(updated);

        assertEquals("Beneitez Baz", repository.findById(saved.id()).orElseThrow().lastName());
    }

    @Test
    void borraYDevuelveSiExistia() {
        Person saved = repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));

        assertTrue(repository.deleteById(saved.id()));
        assertFalse(repository.deleteById(saved.id()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void encuentraPorCorreo() {
        repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));

        assertTrue(repository.findByEmail("jairo@ejemplo.com").isPresent());
        assertTrue(repository.findByEmail("nadie@ejemplo.com").isEmpty());
    }

    @Test
    void losDatosSobrevivenAlReinicioDeLaAplicacion() {
        repository.insert(Person.newPerson("Jairo", "Beneitez", "jairo@ejemplo.com", null));

        SqlitePersonRepository otroRepositorio =
                new SqlitePersonRepository(new ConnectionFactory(jdbcUrl));

        assertEquals(1, otroRepositorio.findAll().size());
    }
}
