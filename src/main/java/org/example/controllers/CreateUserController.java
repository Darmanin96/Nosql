package org.example.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.Conexion.DatabaseConexion;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {

    @FXML
    private Button Aceptar;

    @FXML
    private Button Cancelar;

    @FXML
    private Button Limpiar;

    @FXML
    private PasswordField contraseña;

    @FXML
    private BorderPane root;

    @FXML
    private TextField usuario;


    @FXML
    void onAceptarAction(ActionEvent event) {
        String username = usuario.getText().trim();
        String password = contraseña.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Usuario y contraseña son obligatorios", Alert.AlertType.ERROR);
            return;
        }
        DatabaseConexion.iniciarConexion();
        CouchDbClient dbClient = DatabaseConexion.getDbClient();

        try {
            JsonObject usuariosDoc = dbClient.find(JsonObject.class, "Usuarios_loggin");
            if (usuariosDoc != null && usuariosDoc.has("usuarios")) {
                JsonArray usuariosArray = usuariosDoc.getAsJsonArray("usuarios");
                for (int i = 0; i < usuariosArray.size(); i++) {
                    JsonObject user = usuariosArray.get(i).getAsJsonObject();
                    if (user.has("username") && user.get("username").getAsString().equals(username)) {
                        mostrarAlerta("Error", "El nombre de usuario ya existe", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }

            if (usuariosDoc == null) {
                JsonObject newDoc = new JsonObject();
                newDoc.addProperty("_id", "Usuarios_loggin");
                JsonArray usuariosArray = new JsonArray();
                JsonObject newUser = new JsonObject();
                newUser.addProperty("username", username);
                newUser.addProperty("password", password);
                usuariosArray.add(newUser);
                newDoc.add("usuarios", usuariosArray);
                dbClient.save(newDoc);
            } else {
                JsonArray usuariosArray = usuariosDoc.getAsJsonArray("usuarios");
                JsonObject newUser = new JsonObject();
                newUser.addProperty("username", username);
                newUser.addProperty("password", password);
                usuariosArray.add(newUser);
                JsonObject updatedDoc = new JsonObject();
                updatedDoc.addProperty("_id", usuariosDoc.get("_id").getAsString());
                updatedDoc.addProperty("_rev", usuariosDoc.get("_rev").getAsString());
                updatedDoc.add("usuarios", usuariosArray);
                dbClient.update(updatedDoc);
            }

            mostrarAlerta("Éxito", "Usuario creado con éxito", Alert.AlertType.INFORMATION);
            cerrar();
        } catch (CouchDbException e) {
            mostrarAlerta("Error", "Hubo un error al crear el usuario", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCancelarAction(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Estás seguro de cancelar? Los cambios no se guardarán.");
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alerta.getButtonTypes().setAll(botonSi, botonNo);
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == botonSi) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        }
    }

    // Función para limpiar los campos
    @FXML
    void onLimpiarAction(ActionEvent event) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        usuario.clear();
        contraseña.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }






    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public Button getAceptar() {
        return Aceptar;
    }

    public void setAceptar(Button aceptar) {
        Aceptar = aceptar;
    }

    public Button getCancelar() {
        return Cancelar;
    }

    public void setCancelar(Button cancelar) {
        Cancelar = cancelar;
    }

    public Button getLimpiar() {
        return Limpiar;
    }

    public void setLimpiar(Button limpiar) {
        Limpiar = limpiar;
    }

    public PasswordField getContraseña() {
        return contraseña;
    }

    public void setContraseña(PasswordField contraseña) {
        this.contraseña = contraseña;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TextField getUsuario() {
        return usuario;
    }

    public void setUsuario(TextField usuario) {
        this.usuario = usuario;
    }



}
