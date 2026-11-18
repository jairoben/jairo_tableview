package com.jairoben.agenda.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Contacto de la agenda. Inmutable: cualquier cambio crea una instancia nueva.
 *
 * @param id        clave primaria; {@code 0} significa "todavía no persistido"
 * @param firstName nombre, obligatorio
 * @param lastName  apellidos, obligatorio
 * @param email     correo, obligatorio y único en la agenda
 * @param birthDate fecha de nacimiento, opcional
 */
public record Person(long id, String firstName, String lastName, String email, LocalDate birthDate) {

    public Person {
        Objects.requireNonNull(firstName, "firstName");
        Objects.requireNonNull(lastName, "lastName");
        Objects.requireNonNull(email, "email");
        firstName = firstName.strip();
        lastName = lastName.strip();
        email = email.strip().toLowerCase();
    }

    public static Person newPerson(String firstName, String lastName, String email, LocalDate birthDate) {
        return new Person(0L, firstName, lastName, email, birthDate);
    }

    public Person withId(long newId) {
        return new Person(newId, firstName, lastName, email, birthDate);
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
