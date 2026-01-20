package org.example;

import org.teavm.jso.JSBody;

public class Validator {
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email не может быть пустым");
        }

        // Простая валидация email
        if (!email.contains("@") || !email.contains(".")) {
            return new ValidationResult(false, "Неверный формат email");
        }

        return new ValidationResult(true, "Email валиден");
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return new ValidationResult(false, "Пароль должен быть не менее 6 символов");
        }

        boolean hasDigit = false;
        boolean hasLetter = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isLetter(c)) hasLetter = true;
        }

        if (!hasDigit || !hasLetter) {
            return new ValidationResult(false,
                    "Пароль должен содержать буквы и цифры");
        }

        return new ValidationResult(true, "Пароль валиден");
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    // JavaScript interop для дополнительной валидации
    @JSBody(params = {"email"}, script =
            "return /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/.test(email);")
    public static native boolean jsValidateEmail(String email);
}
