package com.jairoben;

import java.time.LocalDate;

/**
 * Modelo que representa a una persona con nombre, apellidos y fecha de nacimiento.
 * Cada instancia recibe automáticamente un identificador único autoincremental.
 * Sus getters son los que utiliza {@code PropertyValueFactory} para enlazar
 * las columnas de la tabla con los atributos de la entidad.
 */
public class Person {

    /**
     * Contador estático que proporciona el siguiente identificador autoincremental.
     */
    private static int nextId = 1;

    /**
     * Identificador único e inmutable de la persona, asignado en el constructor.
     */
    private final int id;

    /**
     * Nombre de la persona.
     */
    private String firstName;

    /**
     * Apellidos de la persona.
     */
    private String lastName;

    /**
     * Fecha de nacimiento de la persona.
     */
    private LocalDate birthDate;

    /**
     * Crea una persona nueva asignándole el siguiente identificador
     * autoincremental y sus datos personales.
     *
     * @param firstName el nombre de la persona.
     * @param lastName los apellidos de la persona.
     * @param birthDate la fecha de nacimiento de la persona.
     */
    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.id = nextId++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    /**
     * Obtiene el identificador único de la persona.
     *
     * @return el identificador autoincremental asignado al crear la persona.
     */
    public int getId() { return id; }

    /**
     * Obtiene el nombre de la persona.
     *
     * @return el nombre de la persona.
     */
    public String getFirstName() { return firstName; }

    /**
     * Obtiene los apellidos de la persona.
     *
     * @return los apellidos de la persona.
     */
    public String getLastName() { return lastName; }

    /**
     * Obtiene la fecha de nacimiento de la persona.
     *
     * @return la fecha de nacimiento de la persona.
     */
    public LocalDate getBirthDate() { return birthDate; }

    /**
     * Establece el nombre de la persona.
     *
     * @param firstName el nuevo nombre de la persona.
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Establece los apellidos de la persona.
     *
     * @param lastName los nuevos apellidos de la persona.
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Establece la fecha de nacimiento de la persona.
     *
     * @param birthDate la nueva fecha de nacimiento de la persona.
     */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}