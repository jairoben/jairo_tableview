package com.jairoben.agenda.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jairoben.agenda.domain.Person;
import com.jairoben.agenda.domain.PersonRepository;

/** Adaptador de persistencia: {@link PersonRepository} sobre SQLite mediante JDBC. */
public final class SqlitePersonRepository implements PersonRepository {

    private static final String COLUMNS = "id, first_name, last_name, email, birth_date";
    private static final String BASE_SELECT = "SELECT " + COLUMNS + " FROM person ";
    private static final String SEARCH_FILTER =
            "WHERE lower(first_name || ' ' || last_name || ' ' || email) LIKE ? ";

    private final ConnectionFactory connectionFactory;

    public SqlitePersonRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Person> findAll() {
        return query(BASE_SELECT + "ORDER BY last_name COLLATE NOCASE, first_name COLLATE NOCASE");
    }

    @Override
    public List<Person> search(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(
                     BASE_SELECT + SEARCH_FILTER + "ORDER BY last_name COLLATE NOCASE, first_name COLLATE NOCASE")) {
            statement.setString(1, pattern);
            return readAll(statement);
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo buscar en la agenda", e);
        }
    }

    @Override
    public Optional<Person> findById(long id) {
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(BASE_SELECT + "WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo leer el contacto " + id, e);
        }
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(BASE_SELECT + "WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo buscar por correo", e);
        }
    }

    @Override
    public Person insert(Person person) {
        String sql = "INSERT INTO person (first_name, last_name, email, birth_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.firstName());
            statement.setString(2, person.lastName());
            statement.setString(3, person.email());
            statement.setString(4, person.birthDate() == null ? null : person.birthDate().toString());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return person.withId(keys.getLong(1));
                }
                throw new RepositoryException("La base de datos no devolvió el id del contacto", null);
            }
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo guardar el contacto", e);
        }
    }

    @Override
    public void update(Person person) {
        String sql = "UPDATE person SET first_name = ?, last_name = ?, email = ?, birth_date = ? WHERE id = ?";
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.firstName());
            statement.setString(2, person.lastName());
            statement.setString(3, person.email());
            statement.setString(4, person.birthDate() == null ? null : person.birthDate().toString());
            statement.setLong(5, person.id());
            if (statement.executeUpdate() == 0) {
                throw new RepositoryException("No existe el contacto " + person.id(), null);
            }
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo actualizar el contacto", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM person WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo borrar el contacto " + id, e);
        }
    }

    private List<Person> query(String sql) {
        try (Connection connection = connectionFactory.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return readAll(statement);
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo consultar la agenda", e);
        }
    }

    private static List<Person> readAll(PreparedStatement statement) throws SQLException {
        List<Person> people = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                people.add(map(rs));
            }
        }
        return people;
    }

    private static Person map(ResultSet rs) throws SQLException {
        String birth = rs.getString("birth_date");
        return new Person(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                birth == null ? null : LocalDate.parse(birth));
    }
}
