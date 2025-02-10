package org.example.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.Conexion.DatabaseConexion;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LogginController implements Initializable {

    @FXML
    private Button CreateUser;

    @FXML
    private Button clearButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private GridPane root;

    @FXML
    private TextField usuario;

    private TableController tableController;

    private static String usuarioAutenticado;

    @FXML
    void onClearAction(ActionEvent event) {
        usuario.clear();
        passwordField.clear();
    }

    public static String getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    @FXML
    void onCreateUserAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateUser.fxml"));
            Parent root = loader.load();
            CreateUserController controller = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Crear Usuario");

            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de crear usuario.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onLogginButtonAction(ActionEvent event) {
        String username = usuario.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Usuario y contraseña son obligatorios", Alert.AlertType.ERROR);
            return;
        }
        try {
            if (validarCredenciales(username, password)) {
                usuarioAutenticado = username;
                mostrarAlerta("Éxito", "Inicio de sesión correcto", Alert.AlertType.INFORMATION);
                cerrar();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Inventario");
                stage.show();
            } else {
                usuario.clear();
                passwordField.clear();
                mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);

            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ha ocurrido un error al validar las credenciales", Alert.AlertType.ERROR);
        }
    }

    private boolean validarCredenciales(String username, String password) {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();
        try {
            JsonObject usersDoc = dbClient.find(JsonObject.class, "Usuarios_loggin");
            JsonArray usuariosArray = usersDoc.getAsJsonArray("usuarios");
            if (usuariosArray == null || usuariosArray.size() == 0) {
                mostrarAlerta("Error", "No se encontraron usuarios registrados", Alert.AlertType.ERROR);
                return false;
            }

            for (JsonElement userElement : usuariosArray) {
                JsonObject user = userElement.getAsJsonObject();
                String storedUsername = user.get("username").getAsString().trim();
                String storedPassword = user.get("password").getAsString().trim();

                System.out.println("Usuario encontrado: " + storedUsername);

                if (storedUsername.equals(username.trim()) && storedPassword.equals(password.trim())) {
                    System.out.println("Inicio de sesión exitoso");
                    return true;
                }
            }

            mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);

        } catch (CouchDbException e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
            mostrarAlerta("Error", "Ha ocurrido un error al validar las credenciales", Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            mostrarAlerta("Error", "Ha ocurrido un error inesperado al validar las credenciales", Alert.AlertType.ERROR);
        }

        return false;
    }








    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Button getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(Button createUser) {
        CreateUser = createUser;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(Button loginButton) {
        this.loginButton = loginButton;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public GridPane getRoot() {
        return root;
    }

    public void setRoot(GridPane root) {
        this.root = root;
    }

    public TextField getUsuario() {
        return usuario;
    }

    public void setUsuario(TextField usuario) {
        this.usuario = usuario;
    }

    public TableController getTableController() {
        return tableController;
    }

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

    public static void setUsuarioAutenticado(String usuarioAutenticado) {
        LogginController.usuarioAutenticado = usuarioAutenticado;
    }

    public void getUsuarioAutenticado(String usuarioAutenticado) {
        LogginController.usuarioAutenticado = usuarioAutenticado;
    }

}
