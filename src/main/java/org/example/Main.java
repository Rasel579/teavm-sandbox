package org.example;

import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.dom.html.HTMLButtonElement;

public class Main {
    private static HTMLDocument document;
    private static HTMLElement appContainer;

    public static void main(String[] args) {
        document = Window.current().getDocument();
        appContainer = document.getElementById("app");

        if (appContainer == null) {
            Window.alert("Error: element with id='app' not found!");
            return;
        }

        renderLoginForm();

        // Проверяем, есть ли сохраненный пользователь
        String currentUser = AuthService.getCurrentUser();
        if (currentUser != null && !currentUser.isEmpty()) {
            showMessage("Добро пожаловать, " + currentUser + "!");
        }
    }

    private static void renderLoginForm() {
        clearAppContainer();

        // Создаем форму входа
        HTMLElement form = document.createElement("div");
        form.setClassName("auth-form");

        // Заголовок
        HTMLElement title = document.createElement("h2");
        title.setInnerHTML("Вход в систему");
        form.appendChild(title);

        // Поле логина/email
        HTMLElement loginGroup = document.createElement("div");
        loginGroup.setClassName("form-group");

        HTMLElement loginLabel = document.createElement("label");
        loginLabel.setInnerHTML("Логин или Email:");
        loginGroup.appendChild(loginLabel);

        HTMLInputElement loginInput = (HTMLInputElement) document.createElement("input");
        loginInput.setType("text");
        loginInput.setId("login");
        loginInput.setPlaceholder("Введите логин или email");
        loginGroup.appendChild(loginInput);

        form.appendChild(loginGroup);

        // Поле пароля
        HTMLElement passwordGroup = document.createElement("div");
        passwordGroup.setClassName("form-group");

        HTMLElement passwordLabel = document.createElement("label");
        passwordLabel.setInnerHTML("Пароль:");
        passwordGroup.appendChild(passwordLabel);

        HTMLInputElement passwordInput = (HTMLInputElement) document.createElement("input");
        passwordInput.setType("password");
        passwordInput.setId("password");
        passwordInput.setPlaceholder("Введите пароль");
        passwordGroup.appendChild(passwordInput);

        form.appendChild(passwordGroup);

        // Кнопка входа
        HTMLButtonElement loginButton = (HTMLButtonElement) document.createElement("button");
        loginButton.setInnerHTML("Войти");
        loginButton.setClassName("btn btn-primary");
        loginButton.addEventListener("click", evt -> handleLogin(loginInput, passwordInput));
        form.appendChild(loginButton);

        // Кнопка регистрации
        HTMLButtonElement registerButton = (HTMLButtonElement) document.createElement("button");
        registerButton.setInnerHTML("Регистрация");
        registerButton.setClassName("btn btn-secondary");
        registerButton.addEventListener("click", evt -> renderRegistrationForm());
        form.appendChild(registerButton);

        // Сообщения об ошибках
        HTMLElement messageDiv = document.createElement("div");
        messageDiv.setId("message");
        messageDiv.setClassName("message");
        form.appendChild(messageDiv);

        appContainer.appendChild(form);

        // Добавляем обработчик Enter
        loginInput.addEventListener("keypress", evt -> {
            if ("Enter".equals(evt.getType())) {
                handleLogin(loginInput, passwordInput);
            }
        });

        passwordInput.addEventListener("keypress", evt -> {
            if ("Enter".equals(evt.getType())) {
                handleLogin(loginInput, passwordInput);
            }
        });
    }

    private static void renderRegistrationForm() {
        clearAppContainer();

        HTMLElement form = document.createElement("div");
        form.setClassName("auth-form");

        // Заголовок
        HTMLElement title = document.createElement("h2");
        title.setInnerHTML("Регистрация");
        form.appendChild(title);

        // Поле имени пользователя
        addFormField(form, "username", "text", "Имя пользователя:",
                "Введите имя пользователя");

        // Поле email
        addFormField(form, "email", "email", "Email:",
                "Введите email");

        // Поле пароля
        addFormField(form, "regPassword", "password", "Пароль:",
                "Введите пароль");

        // Подтверждение пароля
        addFormField(form, "confirmPassword", "password", "Подтверждение пароля:",
                "Повторите пароль");

        // Кнопки
        HTMLButtonElement registerButton = (HTMLButtonElement) document.createElement("button");
        registerButton.setInnerHTML("Зарегистрироваться");
        registerButton.setClassName("btn btn-primary");
        registerButton.addEventListener("click", Main::handleRegister);
        form.appendChild(registerButton);

        HTMLButtonElement backButton = (HTMLButtonElement) document.createElement("button");
        backButton.setInnerHTML("Назад");
        backButton.setClassName("btn btn-secondary");
        backButton.addEventListener("click", evt -> renderLoginForm());
        form.appendChild(backButton);

        // Сообщения
        HTMLElement messageDiv = document.createElement("div");
        messageDiv.setId("message");
        messageDiv.setClassName("message");
        form.appendChild(messageDiv);

        appContainer.appendChild(form);
    }

    private static void addFormField(HTMLElement form, String id, String type,
                                     String labelText, String placeholder) {
        HTMLElement group = document.createElement("div");
        group.setClassName("form-group");

        HTMLElement label = document.createElement("label");
        label.setInnerHTML(labelText);
        group.appendChild(label);

        HTMLInputElement input = (HTMLInputElement) document.createElement("input");
        input.setType(type);
        input.setId(id);
        input.setPlaceholder(placeholder);
        group.appendChild(input);

        form.appendChild(group);
    }

    private static void handleLogin(HTMLInputElement loginInput, HTMLInputElement passwordInput) {
        String login = loginInput.getValue();
        String password = passwordInput.getValue();

        showMessage("Проверка...");

        AuthService.AuthResponse response = AuthService.authenticate(login, password);

        if (response.isSuccess()) {
            showSuccess("Вход выполнен успешно! Добро пожаловать, " +
                    response.getUser().getUsername());
            renderUserProfile(response.getUser());
        } else {
            showError(response.getMessage());
        }
    }

    private static void handleRegister(JSObject evt) {
        HTMLInputElement usernameInput = (HTMLInputElement) document.getElementById("username");
        HTMLInputElement emailInput = (HTMLInputElement) document.getElementById("email");
        HTMLInputElement passwordInput = (HTMLInputElement) document.getElementById("regPassword");
        HTMLInputElement confirmInput = (HTMLInputElement) document.getElementById("confirmPassword");

        String username = usernameInput.getValue();
        String email = emailInput.getValue();
        String password = passwordInput.getValue();
        String confirm = confirmInput.getValue();

        // Базовая проверка
        if (!password.equals(confirm)) {
            showError("Пароли не совпадают");
            return;
        }

        AuthService.AuthResponse response = AuthService.register(username, email, password);

        if (response.isSuccess()) {
            showSuccess("Регистрация успешна!");
            renderUserProfile(response.getUser());
        } else {
            showError(response.getMessage());
        }
    }

    private static void renderUserProfile(User user) {
        clearAppContainer();

        HTMLElement profile = document.createElement("div");
        profile.setClassName("profile");

        HTMLElement title = document.createElement("h2");
        title.setInnerHTML("Профиль пользователя");
        profile.appendChild(title);

        HTMLElement info = document.createElement("div");
        info.setClassName("user-info");
        info.setInnerHTML(
                "<p><strong>Имя пользователя:</strong> " + user.getUsername() + "</p>" +
                        "<p><strong>Email:</strong> " + user.getEmail() + "</p>" +
                        "<p><strong>Статус:</strong> Авторизован</p>"
        );
        profile.appendChild(info);

        HTMLButtonElement logoutButton = (HTMLButtonElement) document.createElement("button");
        logoutButton.setInnerHTML("Выйти");
        logoutButton.setClassName("btn btn-warning");
        logoutButton.addEventListener("click", evt -> {
            AuthService.logout();
            showMessage("Вы вышли из системы");
            renderLoginForm();
        });
        profile.appendChild(logoutButton);

        appContainer.appendChild(profile);
    }

    private static void showMessage(String text) {
        HTMLElement messageDiv = document.getElementById("message");
        if (messageDiv != null) {
            messageDiv.setInnerHTML(text);
            messageDiv.setClassName("message info");
        }
    }

    private static void showSuccess(String text) {
        HTMLElement messageDiv = document.getElementById("message");
        if (messageDiv != null) {
            messageDiv.setInnerHTML("✅ " + text);
            messageDiv.setClassName("message success");
        }
    }

    private static void showError(String text) {
        HTMLElement messageDiv = document.getElementById("message");
        if (messageDiv != null) {
            messageDiv.setInnerHTML("❌ " + text);
            messageDiv.setClassName("message error");
        }
    }

    private static void clearAppContainer() {
        appContainer.setInnerHTML("");
    }
}