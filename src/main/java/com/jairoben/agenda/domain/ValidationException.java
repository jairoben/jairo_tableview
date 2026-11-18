package com.jairoben.agenda.domain;

/** Error de negocio con los mensajes que se pueden mostrar al usuario. */
public class ValidationException extends RuntimeException {

    private final String fieldMessage;

    public ValidationException(String fieldMessage) {
        super(fieldMessage);
        this.fieldMessage = fieldMessage;
    }

    public String fieldMessage() {
        return fieldMessage;
    }
}
