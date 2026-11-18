package com.jairoben.agenda.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Repositorio en memoria para probar el servicio sin base de datos. */
final class InMemoryPersonRepository implements PersonRepository {

    private final Map<Long, Person> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Person> search(String query) {
        String needle = query.toLowerCase();
        return storage.values().stream()
                .filter(p -> (p.firstName() + " " + p.lastName() + " " + p.email()).toLowerCase().contains(needle))
                .toList();
    }

    @Override
    public Optional<Person> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return storage.values().stream().filter(p -> p.email().equals(email)).findFirst();
    }

    @Override
    public Person insert(Person person) {
        Person saved = person.withId(sequence.incrementAndGet());
        storage.put(saved.id(), saved);
        return saved;
    }

    @Override
    public void update(Person person) {
        storage.put(person.id(), person);
    }

    @Override
    public boolean deleteById(long id) {
        return storage.remove(id) != null;
    }
}
