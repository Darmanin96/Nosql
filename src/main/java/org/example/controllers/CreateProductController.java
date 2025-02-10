package org.example.controllers;

import com.google.gson.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateProductController implements Initializable {



    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<String> estado;

    @FXML
    private DatePicker fecha;

    @FXML
    private TextField nombre;

    @FXML
    private TextField precio;

    @FXML
    private BorderPane root;

    @FXML
    private Spinner<Integer> stock;

    private TableController tableController;

    @FXML
    private ChoiceBox<String> categoria;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FormatoPrecio();
        stock.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1));
        cargarEstados();
        cargarCategorias();


    }

    public void cargarEstados() {
        try {
            CouchDbClient dbClient = DatabaseConexion.getDbClient();
            JsonObject doc = dbClient.find(JsonObject.class, "Estados");

            if (doc != null && doc.has("estados")) {
                JsonArray estados = doc.getAsJsonArray("estados");

                if (estados.size() > 0) {
                    estado.getItems().clear();
                    for (int i = 0; i < estados.size(); i++) {
                        estado.getItems().add(estados.get(i).getAsString());
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No hay estados disponibles", "El documento de estados está vacío.");
                }
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el documento de estados", "No se pudieron cargar los estados.");
            }
        } catch (CouchDbException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Problema de conexión", "Detalles: " + e.getMessage());

        }
    }

    public void cargarCategorias() {
        try {
            CouchDbClient dbClient = DatabaseConexion.getDbClient();
            JsonObject doc = dbClient.find(JsonObject.class, "Categoria");

            if (doc != null && doc.has("categorias")) {
                JsonArray categoriasArray = doc.getAsJsonArray("categorias");

                if (categoriasArray.size() > 0) {
                    categoria.getItems().clear();

                    for (JsonElement elemento : categoriasArray) {
                        JsonObject categoriaObj = elemento.getAsJsonObject();
                        if (categoriaObj.has("nombre")) {
                            categoria.getItems().add(categoriaObj.get("nombre").getAsString());
                        }
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No hay categorías disponibles", "El documento de categorías está vacío.");
                }
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el documento de categorías", "No se pudieron cargar las categorías.");
            }
        } catch (CouchDbException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Problema de conexión", "Detalles: " + e.getMessage());
        }
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }


    private void FormatoPrecio() {
        precio.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[0-9]*([,]?[0-9]{0,2})?$")) {
                return change;
            } else {
                alerta();
                return null;
            }
        }));
    }

    private void alerta() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Formato precio incorrecto");
        alert.setContentText("Por favor, ingrese un precio válido.");
        alert.show();
    }





    @FXML
    void onAceptarAction(ActionEvent event) {
        String desc = description.getText().trim();
        String estadoProducto = estado.getValue().toString();
        LocalDate fechaProducto = fecha.getValue();
        String nombreProducto = nombre.getText().trim();
        String precioProducto = precio.getText().trim();
        Integer stockProducto = stock.getValue();
        String categoriaProducto = categoria.getValue().toString();
        if (categoriaProducto.isEmpty() || nombreProducto.isEmpty() || desc.isEmpty() || estadoProducto.isEmpty() || precioProducto.isEmpty() || fechaProducto == null || stockProducto == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Campos incompletos");
            alert.setContentText("Por favor, completa todos los campos.");
            alert.showAndWait();
            return;
        }

        try {
            CouchDbClient dbClient = DatabaseConexion.getDbClient();
            JsonObject inventarioDoc = dbClient.find(JsonObject.class, "Inventario");
            if (inventarioDoc != null && inventarioDoc.has("items")) {
                JsonArray itemsArray = inventarioDoc.getAsJsonArray("items");
                int lastId = itemsArray.size() > 0 ? itemsArray.get(itemsArray.size() - 1).getAsJsonObject().get("id").getAsInt() : 0;
                int newId = lastId + 1;
                JsonObject newProduct = new JsonObject();
                newProduct.addProperty("id", newId);
                newProduct.addProperty("nombre", nombreProducto);
                newProduct.addProperty("descripcion", desc);
                newProduct.addProperty("estado", estadoProducto);
                newProduct.addProperty("fecha", fechaProducto.format(DateTimeFormatter.ISO_LOCAL_DATE));
                newProduct.addProperty("stock", stockProducto);
                newProduct.addProperty("precio", Double.parseDouble(precioProducto.replace(",", ".")));
                newProduct.addProperty("categoria", categoriaProducto);
                itemsArray.add(newProduct);
                JsonObject updatedDoc = new JsonObject();
                updatedDoc.addProperty("_id", inventarioDoc.get("_id").getAsString());
                updatedDoc.addProperty("_rev", inventarioDoc.get("_rev").getAsString());
                updatedDoc.add("items", itemsArray);
                dbClient.update(updatedDoc);
                tableController.cargarInventario();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Éxito");
                successAlert.setHeaderText("Producto añadido correctamente");
                successAlert.showAndWait();
                cerrar();
            }


        } catch (CouchDbException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("No se pudo añadir el producto");
            errorAlert.setContentText("Error en la base de datos: " + e.getMessage());
            errorAlert.showAndWait();
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

        fecha.setValue(null);
        precio.clear();
        stock.getValueFactory().setValue(1);

    }


    public TextArea getDescription() {
        return description;
    }

    public void setDescription(TextArea description) {
        this.description = description;
    }

    public DatePicker getFecha() {
        return fecha;
    }

    public void setFecha(DatePicker fecha) {
        this.fecha = fecha;
    }

    public TextField getPrecio() {
        return precio;
    }

    public void setPrecio(TextField precio) {
        this.precio = precio;
    }

    public TextField getNombre() {
        return nombre;
    }

    public void setNombre(TextField nombre) {
        this.nombre = nombre;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public Spinner<Integer> getStock() {
        return stock;
    }

    public void setStock(Spinner<Integer> stock) {
        this.stock = stock;
    }

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

}
