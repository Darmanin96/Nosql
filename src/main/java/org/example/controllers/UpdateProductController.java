package org.example.controllers;

import com.google.gson.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.Conexion.DatabaseConexion;
import org.example.models.Inventario;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdateProductController implements Initializable {

    @FXML
    private Button Aceptar;

    @FXML
    private Button Cancelar;

    @FXML
    private Button Limpiar;

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

    private Inventario inventario;

    private TableController table;

    @FXML
    private ChoiceBox<String> categoria;


    private CreateProductController createProductController;



    private TableController tableController;

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
        if (inventario != null) {
            nombre.setText(inventario.getNombreInventario());
            description.setText(inventario.getDescripcionInventario());
            estado.setValue(inventario.getEstado());
            fecha.setValue(inventario.getFechaInventario());
            stock.getValueFactory().setValue(inventario.getStock());
            precio.setText(String.valueOf(inventario.getPrecio()));
            categoria.setValue(inventario.getCategoria());

        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Inventario no válido", "No se pudo cargar la información del producto.");
        }
    }

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }



    @FXML
    void onCancelarAction(ActionEvent event) {

    }


    @FXML
    void onLimpiarAction(ActionEvent event) {
        nombre.clear();
        description.clear();
        estado.setValue(null);
        fecha.setValue(null);
        stock.getValueFactory().setValue(0);
        precio.clear();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            cargarEstado();
            cargarCategorias();
        if (stock != null) {
            stock.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        }
    }

    private void cargarCategorias() {
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

    private void cargarEstado(){
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






    @FXML
    void onAceptarAction(ActionEvent event) {
        if (inventario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Inventario no disponible", "No se puede actualizar un inventario inexistente.");
            return;
        }

        try {
            CouchDbClient dbClient = DatabaseConexion.getDbClient();
            JsonObject inventarioDoc = dbClient.find(JsonObject.class, "Inventario");

            if (inventarioDoc != null && inventarioDoc.has("items")) {
                JsonArray items = inventarioDoc.getAsJsonArray("items");
                JsonArray newItemsArray = new JsonArray();

                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    if (item.get("id").getAsInt() == inventario.getIdInventario()) {
                        item.addProperty("nombre", nombre.getText());
                        item.addProperty("descripcion", description.getText());
                        item.addProperty("estado", estado.getValue() != null ? estado.getValue() : "No disponible"); // Evitar null en estado
                        item.addProperty("fecha", fecha.getValue().toString());
                        item.addProperty("stock", stock.getValue());
                        item.addProperty("precio", Double.parseDouble(precio.getText()));
                    }
                    newItemsArray.add(item);
                }
                inventarioDoc.add("items", newItemsArray);
                tableController.cargarInventario();
                dbClient.update(inventarioDoc);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto actualizado", "El producto ha sido actualizado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el documento de inventario", "No se pudo actualizar.");
            }
        } catch (CouchDbException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar", "Detalles: " + e.getMessage());
        }
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

    public TextArea getDescription() {
        return description;
    }

    public void setDescription(TextArea description) {
        this.description = description;
    }

    public ChoiceBox<String> getEstado() {
        return estado;
    }

    public void setEstado(ChoiceBox<String> estado) {
        this.estado = estado;
    }

    public DatePicker getFecha() {
        return fecha;
    }

    public void setFecha(DatePicker fecha) {
        this.fecha = fecha;
    }

    public TextField getNombre() {
        return nombre;
    }

    public void setNombre(TextField nombre) {
        this.nombre = nombre;
    }

    public TextField getPrecio() {
        return precio;
    }

    public void setPrecio(TextField precio) {
        this.precio = precio;
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

    public Inventario getInventario() {
        return inventario;
    }
}
