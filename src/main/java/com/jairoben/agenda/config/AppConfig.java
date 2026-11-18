package com.jairoben.agenda.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración de la aplicación leída de {@code application.properties}.
 * Se puede sobreescribir la ruta de la base de datos con {@code -Dagenda.db=...}.
 */
public record AppConfig(String jdbcUrl, String windowTitle) {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    public static AppConfig load() {
        Properties properties = readProperties();
        String rawPath = System.getProperty(
                "agenda.db",
                properties.getProperty("agenda.db.path", "~/.agendafx/agenda.db"));
        Path databaseFile = Path.of(expandHome(rawPath)).toAbsolutePath();
        createParentDirectory(databaseFile);
        log.info("Base de datos: {}", databaseFile);
        return new AppConfig(
                "jdbc:sqlite:" + databaseFile,
                properties.getProperty("agenda.title", "AgendaFX"));
    }

    private static Properties readProperties() {
        Properties properties = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer application.properties", e);
        }
        return properties;
    }

    private static String expandHome(String path) {
        String home = System.getProperty("user.home");
        if (path.startsWith("~/") || path.startsWith("~\\")) {
            return home + path.substring(1);
        }
        return path;
    }

    private static void createParentDirectory(Path file) {
        Path parent = file.getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de datos " + parent, e);
        }
    }
}
