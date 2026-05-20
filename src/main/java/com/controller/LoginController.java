package com.controller;

import com.dao.UserDAO;
import com.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    private final Stage stage;
    private StackPane root;
    private Region ambientOrbOne;
    private Region ambientOrbTwo;
    private double dragOffsetX;
    private double dragOffsetY;

    public LoginController(Stage stage) {
        this.stage = stage;
        createView();
    }

    public Parent getView() {
        return root;
    }

    private void createView() {
        root = new StackPane();
        root.getStyleClass().add("login-root");

        StackPane ambient = new StackPane();
        ambient.setMouseTransparent(true);

        ambientOrbOne = createAmbientOrb("ambient-orb-one", 420, 420);
        ambientOrbTwo = createAmbientOrb("ambient-orb-two", 320, 320);
        StackPane.setAlignment(ambientOrbOne, Pos.TOP_LEFT);
        StackPane.setAlignment(ambientOrbTwo, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(ambientOrbOne, new Insets(-120, 0, 0, -120));
        StackPane.setMargin(ambientOrbTwo, new Insets(0, -100, -120, 0));
        ambient.getChildren().addAll(ambientOrbOne, ambientOrbTwo);

        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("login-shell");
        shell.setTop(createWindowBar());
        shell.setCenter(createCenterCard());

        root.getChildren().addAll(ambient, shell);
        initParallax();
    }

    private Region createAmbientOrb(String styleClass, double width, double height) {
        Region orb = new Region();
        orb.getStyleClass().add(styleClass);
        orb.setPrefSize(width, height);
        orb.setEffect(new GaussianBlur(120));
        return orb;
    }

    private Parent createWindowBar() {
        HBox titleBar = new HBox(14);
        titleBar.getStyleClass().add("window-bar");
        titleBar.setPadding(new Insets(14, 18, 14, 18));
        titleBar.setAlignment(Pos.CENTER_LEFT);

        Label brand = new Label("PulseBeat");
        brand.getStyleClass().add("window-brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_RIGHT);

        Button minimizeButton = createWindowControl("-", "window-minimize");
        Button maximizeButton = createWindowControl("□", "window-maximize");
        Button closeButton = createWindowControl("X", "window-close");

        minimizeButton.setOnAction(e -> stage.setIconified(true));
        maximizeButton.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        closeButton.setOnAction(e -> stage.close());

        controls.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
        titleBar.getChildren().addAll(brand, spacer, controls);

        titleBar.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });

        titleBar.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            }
        });

        return titleBar;
    }

    private Button createWindowControl(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("window-control", styleClass);
        return button;
    }

    private Parent createCenterCard() {
        UserDAO userDAO = new UserDAO();

        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("login-card");
        formCard.setPadding(new Insets(32));
        formCard.setMaxWidth(420);
        formCard.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Вход");
        formTitle.getStyleClass().add("form-title");

        TextField loginField = new TextField();
        loginField.setPromptText("Логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        Label message = new Label();
        message.getStyleClass().add("form-message");
        message.setWrapText(true);

        Button loginButton = new Button("Войти");
        loginButton.getStyleClass().add("accent-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Создать аккаунт");
        registerButton.getStyleClass().add("glass-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> {
            try {
                User user = userDAO.login(loginField.getText(), passwordField.getText());
                if (user != null) {
                    MainController mainController = new MainController(stage, user);
                    stage.getScene().setRoot(mainController.getView());
                } else {
                    message.setText("Неверный логин или пароль.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Не удалось выполнить вход. Проверь подключение к базе данных.");
            }
        });

        registerButton.setOnAction(e -> {
            try {
                userDAO.register(loginField.getText(), passwordField.getText());
                message.setText("Аккаунт создан. Теперь можно войти.");
            } catch (Exception ex) {
                message.setText("Не удалось зарегистрироваться. Возможно, логин уже занят.");
            }
        });

        formCard.getChildren().addAll(formTitle, loginField, passwordField, loginButton, registerButton, message);

        StackPane container = new StackPane(formCard);
        container.setPadding(new Insets(40));
        return container;
    }

    private void initParallax() {
        root.setOnMouseMoved(event -> {
            double xRatio = event.getSceneX() / Math.max(root.getWidth(), 1) - 0.5;
            double yRatio = event.getSceneY() / Math.max(root.getHeight(), 1) - 0.5;

            ambientOrbOne.setTranslateX(xRatio * 24);
            ambientOrbOne.setTranslateY(yRatio * 18);
            ambientOrbTwo.setTranslateX(-xRatio * 18);
            ambientOrbTwo.setTranslateY(-yRatio * 14);
        });
    }
}
