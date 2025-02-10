package org.example.controllers;

import com.google.gson.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.example.Conexion.*;
import org.lightcouch.*;

import java.net.*;
import java.util.*;

public class CreateCatagoriaController implements Initializable {


    @FXML
    private TextArea description;

    @FXML
    private TextField nombre;

    @FXML
    private BorderPane root;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @FXML
    void onAñadirAction(ActionEvent event) {
        String nombreCategoria = nombre.getText().trim();
        String descripcionCategoria = description.getText().trim();

        if (nombreCategoria.isEmpty() || descripcionCategoria.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Campos incompletos");
            alert.setContentText("Por favor, complete todos los campos.");
            alert.show();
            return;
        }

        try {
            CouchDbClient dbClient = DatabaseConexion.getDbClient();
            JsonObject categoriaDoc = dbClient.find(JsonObject.class, "Categoria");

            if (categoriaDoc != null && categoriaDoc.has("categorias")) {
                JsonArray categoriasArray = categoriaDoc.getAsJsonArray("categorias");

                // Generar nuevo ID automáticamente
                String newId = "C" + String.format("%03d", categoriasArray.size() + 1);

                JsonObject nuevaCategoria = new JsonObject();
                nuevaCategoria.addProperty("id", newId);
                nuevaCategoria.addProperty("nombre", nombreCategoria);
                nuevaCategoria.addProperty("descripcion", descripcionCategoria);

                categoriasArray.add(nuevaCategoria);

                // Actualizar documento en CouchDB
                JsonObject updatedDoc = new JsonObject();
                updatedDoc.addProperty("_id", categoriaDoc.get("_id").getAsString());
                updatedDoc.addProperty("_rev", categoriaDoc.get("_rev").getAsString());
                updatedDoc.add("categorias", categoriasArray);

                dbClient.update(updatedDoc);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Éxito");
                successAlert.setHeaderText("Categoría añadida correctamente");
                successAlert.setContentText("La categoría '" + nombreCategoria + "' ha sido agregada.");
                successAlert.showAndWait();
                cerrar();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("No se encontró el documento de categorías.");
                errorAlert.setContentText("Verifique la base de datos.");
                errorAlert.show();
            }
        } catch (CouchDbException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error de base de datos");
            errorAlert.setHeaderText("No se pudo añadir la categoría");
            errorAlert.setContentText("Detalles: " + e.getMessage());
            errorAlert.show();
        }
    }


    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }



    @FXML
    void onCancelarAction(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Estás seguro?");
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alerta.getButtonTypes().setAll(botonSi, botonNo);
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == botonSi) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        } else {

        }
    }

    @FXML
    void onLimpiarAction(ActionEvent event) {
        nombre.clear();
        description.clear();

    }




}
