package com.jairoben;

/**
 * Lanzador auxiliar de la aplicación.
 * <p>
 * JavaFX exige que la clase con {@code main} no extienda
 * {@link javafx.application.Application} cuando la ejecución se hace por
 * classpath (VS Code, IntelliJ sin configuración de módulos...); de lo
 * contrario falla con «JavaFX runtime components are missing».
 * Esta clase solo delega en {@link App#main(String[])}.
 * </p>
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Punto de entrada que arranca la aplicación JavaFX.
     *
     * @param args argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
