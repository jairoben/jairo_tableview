package com.jairoben.agenda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.jairoben.agenda.config.AppConfig;
import com.jairoben.agenda.domain.PersonService;
import com.jairoben.agenda.domain.PersonValidator;
import com.jairoben.agenda.infrastructure.ConnectionFactory;
import com.jairoben.agenda.infrastructure.SchemaMigrator;
import com.jairoben.agenda.infrastructure.SqlitePersonRepository;
import com.jairoben.agenda.ui.MainController;

/** Punto de entrada JavaFX: monta configuración, base de datos, servicio y vista. */
public final class AgendaApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AppConfig config = AppConfig.load();

        ConnectionFactory connectionFactory = new ConnectionFactory(config.jdbcUrl());
        new SchemaMigrator(connectionFactory).migrate();

        PersonService service = new PersonService(
                new SqlitePersonRepository(connectionFactory),
                new PersonValidator());

        FXMLLoader loader = new FXMLLoader(AgendaApp.class.getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.init(service);

        Scene scene = new Scene(root, 1060, 640);
        scene.getStylesheets().add(AgendaApp.class.getResource("/css/agenda.css").toExternalForm());

        stage.setTitle(config.windowTitle());
        stage.setMinWidth(900);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
