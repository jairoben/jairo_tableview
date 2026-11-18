package com.jairoben.agenda;

/**
 * Lanzador sin herencia de {@link javafx.application.Application}: evita el error
 * "JavaFX runtime components are missing" al arrancar desde el classpath.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        AgendaApp.main(args);
    }
}
