package com.jairoben.agenda.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre conexiones a la base de datos. Una conexión por operación: simple y sin fugas. */
public final class ConnectionFactory {

    private final String jdbcUrl;

    public ConnectionFactory(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }
}
