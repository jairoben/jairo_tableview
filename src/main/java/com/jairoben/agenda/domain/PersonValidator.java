package com.jairoben.agenda.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de validación del contacto. Vive en el dominio, no en la interfaz,
 * para que la UI y los tests compartan exactamente el mismo criterio.
 */
public final class PersonValidator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int MAX_NAME = 60;
    private static final int MAX_EMAIL = 120;

    public void validate(String firstName, String lastName, String email, LocalDate birthDate) {
        List<String> errors = new ArrayList<>();

        if (isBlank(firstName)) {
            errors.add("El nombre es obligatorio.");
        } else if (firstName.strip().length() > MAX_NAME) {
            errors.add("El nombre no puede pasar de " + MAX_NAME + " caracteres.");
        }

        if (isBlank(lastName)) {
            errors.add("Los apellidos son obligatorios.");
        } else if (lastName.strip().length() > MAX_NAME) {
            errors.add("Los apellidos no pueden pasar de " + MAX_NAME + " caracteres.");
        }

        if (isBlank(email)) {
            errors.add("El correo es obligatorio.");
        } else if (email.strip().length() > MAX_EMAIL) {
            errors.add("El correo no puede pasar de " + MAX_EMAIL + " caracteres.");
        } else if (!EMAIL.matcher(email.strip()).matches()) {
            errors.add("El correo no tiene un formato válido.");
        }

        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            errors.add("La fecha de nacimiento no puede estar en el futuro.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("\n", errors));
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
