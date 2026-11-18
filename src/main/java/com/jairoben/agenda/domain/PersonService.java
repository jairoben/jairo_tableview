package com.jairoben.agenda.domain;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos de uso de la agenda: alta, edición, borrado y búsqueda de contactos.
 * Aquí viven las reglas que no son de interfaz ni de base de datos.
 */
public final class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository repository;
    private final PersonValidator validator;

    public PersonService(PersonRepository repository, PersonValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public List<Person> search(String query) {
        if (query == null || query.isBlank()) {
            return repository.findAll();
        }
        return repository.search(query.strip());
    }

    public Person create(String firstName, String lastName, String email, LocalDate birthDate) {
        validator.validate(firstName, lastName, email, birthDate);
        String normalizedEmail = normalize(email);
        repository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new ValidationException("Ya existe un contacto con el correo " + normalizedEmail + ".");
        });
        Person saved = repository.insert(Person.newPerson(firstName, lastName, normalizedEmail, birthDate));
        log.info("Contacto creado id={} email={}", saved.id(), saved.email());
        return saved;
    }

    public Person update(long id, String firstName, String lastName, String email, LocalDate birthDate) {
        validator.validate(firstName, lastName, email, birthDate);
        repository.findById(id).orElseThrow(
                () -> new ValidationException("El contacto que intentas modificar ya no existe."));
        String normalizedEmail = normalize(email);
        repository.findByEmail(normalizedEmail)
                .filter(other -> other.id() != id)
                .ifPresent(other -> {
                    throw new ValidationException("Ese correo ya lo usa " + other.fullName() + ".");
                });
        Person updated = new Person(id, firstName, lastName, normalizedEmail, birthDate);
        repository.update(updated);
        log.info("Contacto actualizado id={}", id);
        return updated;
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new ValidationException("El contacto que intentas borrar ya no existe.");
        }
        log.info("Contacto eliminado id={}", id);
    }

    public long count() {
        return repository.findAll().size();
    }

    private static String normalize(String email) {
        return email.strip().toLowerCase();
    }
}
