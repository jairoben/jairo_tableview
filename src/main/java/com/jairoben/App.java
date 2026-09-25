package com.jairoben;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Punto de entrada de la aplicación JavaFX.
 * Carga la vista principal desde el archivo FXML {@code /fxml/primary.fxml},
 * la asocia a la ventana y fija los límites mínimo y máximo de su tamaño.
 */
public class App extends Application {

    /**
     * Escena única de la aplicación, compartida por todas las vistas cargadas.
     */
    private static Scene scene;

    /**
     * Inicia la aplicación JavaFX: crea la escena con la vista principal,
     * la asigna a la ventana, le impone los límites de tamaño
     * (mínimo 520x560, máximo 760x820) y la muestra.
     *
     * @param stage la ventana principal sobre la que se muestra la escena.
     * @throws IOException si el archivo FXML {@code /fxml/primary.fxml} no puede cargarse.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Locale locale = Locale.forLanguageTag("es");
        ResourceBundle strings = ResourceBundle.getBundle("strings", locale);

        scene = new Scene(loadFXML("primary", strings));
        stage.setScene(scene);
        stage.setTitle(strings.getString("window.title"));
        stage.setMinWidth(520);
        stage.setMinHeight(560);
        stage.setMaxWidth(760);
        stage.setMaxHeight(820);
        stage.show();
    }

    /**
     * Carga una vista desde un archivo FXML ubicado en el paquete {@code /fxml/},
     * pasándole el bundle de textos para que las claves {@code %...} del FXML
     * se resuelvan.
     *
     * @param fxml el nombre del archivo FXML sin extensión.
     * @param strings bundle de recursos con los textos de la aplicación.
     * @return el nodo raíz definido en el archivo FXML cargado.
     * @throws IOException si el archivo FXML no existe o no puede leerse.
     */
    private static Parent loadFXML(String fxml, ResourceBundle strings) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"), strings);
        return fxmlLoader.load();
    }

    /**
     * Método principal que arranca la aplicación JavaFX.
     *
     * @param args argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        launch();
    }

}