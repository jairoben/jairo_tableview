package com.jairoben.agenda.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepara la base de datos al arrancar. El esquema vive en
 * {@code src/main/resources/db/schema.sql} y las sentencias son idempotentes,
 * así que se puede ejecutar en cada arranque sin miedo.
 */
public final class SchemaMigrator {

    private static final Logger log = LoggerFactory.getLogger(SchemaMigrator.class);
    private static final String SCHEMA = "/db/schema.sql";

    private final ConnectionFactory connectionFactory;

    public SchemaMigrator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void migrate() {
        String script = readSchema();
        try (Connection connection = connectionFactory.open();
             Statement statement = connection.createStatement()) {
            for (String sql : script.split(";")) {
                String trimmed = sql.strip();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
            log.info("Esquema verificado en {}", connectionFactory.jdbcUrl());
        } catch (SQLException e) {
            throw new RepositoryException("No se pudo preparar el esquema de la base de datos", e);
        }
    }

    private static String readSchema() {
        try (InputStream in = SchemaMigrator.class.getResourceAsStream(SCHEMA)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + SCHEMA + " en el classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("No se pudo leer el esquema " + SCHEMA, e);
        }
    }
}
