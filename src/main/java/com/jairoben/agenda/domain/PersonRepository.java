package com.jairoben.agenda.domain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia. El dominio no sabe si detrás hay SQLite, MariaDB
 * o una lista en memoria: solo pide estas operaciones.
 */
public interface PersonRepository {

    List<Person> findAll();

    List<Person> search(String query);

    Optional<Person> findById(long id);

    Optional<Person> findByEmail(String email);

    Person insert(Person person);

    void update(Person person);

    boolean deleteById(long id);
}
