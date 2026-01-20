package org.example;

import org.teavm.jso.JSBody;

public class AuthService {
    // Имитация базы данных пользователей
    private static final User[] USERS = {
            new User("admin", "admin@example.com"),
            new User("user1", "user1@example.com"),
            new User("test", "test@example.com")
    };

    public static AuthResponse authenticate(String usernameOrEmail, String password) {
        // В реальном приложении здесь был бы запрос к серверу

        // Имитация проверки
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return new AuthResponse(false, "Введите логин или email", null);
        }

        if (password == null || password.trim().isEmpty()) {
            return new AuthResponse(false, "Введите пароль", null);
        }

        // Поиск пользователя
        for (User user : USERS) {
            if (user.getUsername().equals(usernameOrEmail) ||
                    user.getEmail().equals(usernameOrEmail)) {

                // Имитация проверки пароля (в реальном приложении - хэш)
                if (isValidPassword(usernameOrEmail, password)) {
                    user.setAuthenticated(true);
                    saveToLocalStorage("currentUser", user.getUsername());
                    return new AuthResponse(true, "Успешный вход!", user);
                }
            }
        }

        return new AuthResponse(false, "Неверные учетные данные", null);
    }

    public static AuthResponse register(String username, String email, String password) {
        // Валидация
        Validator.ValidationResult emailResult = Validator.validateEmail(email);
        if (!emailResult.isValid()) {
            return new AuthResponse(false, emailResult.getMessage(), null);
        }

        Validator.ValidationResult passwordResult = Validator.validatePassword(password);
        if (!passwordResult.isValid()) {
            return new AuthResponse(false, passwordResult.getMessage(), null);
        }

        // Проверка существующего пользователя
        for (User user : USERS) {
            if (user.getUsername().equals(username)) {
                return new AuthResponse(false, "Пользователь уже существует", null);
            }
        }

        // Имитация создания пользователя
        User newUser = new User(username, email);
        newUser.setAuthenticated(true);
        saveToLocalStorage("currentUser", username);

        return new AuthResponse(true, "Регистрация успешна!", newUser);
    }

    public static void logout() {
        removeFromLocalStorage("currentUser");
    }

    public static String getCurrentUser() {
        return getFromLocalStorage("currentUser");
    }

    // Вспомогательные методы

    private static boolean isValidPassword(String username, String password) {
        // Имитация: пароль = username + "123"
        return password.equals(username + "123");
    }

    // JavaScript interop для localStorage
    @JSBody(params = {"key", "value"}, script =
            "localStorage.setItem(key, value);")
    private static native void saveToLocalStorage(String key, String value);

    @JSBody(params = {"key"}, script =
            "return localStorage.getItem(key);")
    private static native String getFromLocalStorage(String key);

    @JSBody(params = {"key"}, script =
            "localStorage.removeItem(key);")
    private static native void removeFromLocalStorage(String key);

    // Класс ответа авторизации
    public static class AuthResponse {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResponse(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}
