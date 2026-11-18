package com.jairoben.agenda.infrastructure;

/** Fallo de persistencia: se lanza cuando la base de datos no responde como se espera. */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
