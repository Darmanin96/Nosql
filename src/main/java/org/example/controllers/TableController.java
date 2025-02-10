package org.example.controllers;

import com.google.gson.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Conexion.DatabaseConexion;
import org.example.models.Inventario;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class TableController implements Initializable {

    @FXML
    private Button Eliminar;

    @FXML
    private Button Filtrar;

    @FXML
    private TableColumn<Inventario, String> Description;

    @FXML
    private TableColumn<Inventario, String> Estado;

    @FXML
    private TableColumn<Inventario, Date> fecha;

    @FXML
    private TableColumn<Inventario, String> Nombre;

    @FXML
    private TableColumn<Inventario, Integer> id;


    @FXML
    private TableColumn<Inventario, Integer> Stock;

    @FXML
    private TableColumn<Inventario, Double> Precio;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Inventario> tableInventario;

    @FXML
    private TableColumn<Inventario, String> Categoria;

    @FXML
    private ComboBox<String> Nombre_Filtrar;

    private ObservableList<Inventario> inventarioList = FXCollections.observableArrayList();

    private LogginController logginController;


    @FXML
    void onAñadirButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateProduct.fxml"));
            Parent root = loader.load();
            CreateProductController createProductController = loader.getController();
            createProductController.setTableController(this);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Crear Producto");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la ventana de creación de producto.");
            alert.setContentText("Detalles del error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void cerrarSesion(Event event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Loggin.fxml"));
            Parent root = loader.load();
            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onDarseAction(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Estás seguro que desea eliminar este usuario?");
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alerta.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == botonSi) {
            eliminarUsuario(LogginController.getUsuarioAutenticado());
            cerrarSesion(event);
        }
    }

    private void eliminarUsuario(String username) {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();

        try {
            JsonObject doc = dbClient.find(JsonObject.class, "Usuarios_loggin");
            JsonArray usuarios = doc.getAsJsonArray("usuarios");
            JsonArray nuevaListaUsuarios = new JsonArray();
            for (JsonElement user : usuarios) {
                JsonObject obj = user.getAsJsonObject();
                if (!obj.get("username").getAsString().equals(username)) {
                    nuevaListaUsuarios.add(obj);
                }
            }
            doc.add("usuarios", nuevaListaUsuarios);
            dbClient.update(doc);

            System.out.println("Usuario eliminado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @FXML
    void onEliminarAction(ActionEvent event) {
        Inventario tableSeleccionada = tableInventario.getSelectionModel().getSelectedItem();

        if (tableSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No has seleccionado ningún elemento");
            alert.setContentText("Por favor, debes seleccionar un elemento.");
            alert.showAndWait();
        } else {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este elemento?");
            confirmacion.setContentText("Nombre: " + tableSeleccionada.getNombreInventario());
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    CouchDbClient dbClient = DatabaseConexion.getDbClient();
                    JsonObject doc = dbClient.find(JsonObject.class, "Inventario");
                    if (doc != null) {
                        JsonArray itemsArray = doc.getAsJsonArray("items");
                        JsonArray newItemsArray = new JsonArray();

                        for (int i = 0; i < itemsArray.size(); i++) {
                            JsonObject itemObject = itemsArray.get(i).getAsJsonObject();
                            if (itemObject.get("id").getAsInt() != tableSeleccionada.getIdInventario()) {
                                newItemsArray.add(itemObject);
                            }
                        }

                        JsonObject updatedDoc = new JsonObject();
                        updatedDoc.addProperty("_id", doc.get("_id").getAsString());
                        updatedDoc.addProperty("_rev", doc.get("_rev").getAsString());
                        updatedDoc.add("items", newItemsArray);
                        dbClient.update(updatedDoc);
                        tableInventario.getItems().remove(tableSeleccionada);
                        actualizarFiltrar();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Eliminado");
                        successAlert.setHeaderText("El inventario ha sido eliminado correctamente.");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("No se encontró el documento de Inventario.");
                        errorAlert.showAndWait();
                    }
                } catch (CouchDbException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Hubo un problema al eliminar el elemento.");
                    errorAlert.setContentText("Mensaje del error: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
    }

    @FXML
    void onDesconectarAction(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Estás seguro que desea desconectarse?");
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alerta.getButtonTypes().setAll(botonSi, botonNo);
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == botonSi) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Loggin.fxml"));
                Parent nuevoRoot = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(nuevoRoot));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void actualizarFiltrar() {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();
        try {
            JsonObject inventarioDoc = dbClient.find(JsonObject.class, "Inventario");
            if (inventarioDoc != null && inventarioDoc.has("items")) {
                JsonArray items = inventarioDoc.getAsJsonArray("items");
                ObservableList<String> nombreList = FXCollections.observableArrayList();
                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    nombreList.add(item.get("nombre").getAsString());
                }
                Nombre_Filtrar.setItems(nombreList);
            }
        } catch (CouchDbException e) {
            System.out.println("Error al actualizar la lista de nombres: " + e.getMessage());
        }
    }


    @FXML
    void onFiltrarAction(ActionEvent event) {
        String nombreBuscado = Nombre_Filtrar.getValue();

        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            cargarInventario();
        } else {
            filtrarPorNombre(nombreBuscado);
        }
    }

    private void filtrarPorNombre(String nombreBuscado) {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();
        try {
            JsonObject inventarioDoc = dbClient.find(JsonObject.class, "Inventario");

            if (inventarioDoc != null && inventarioDoc.has("items")) {
                JsonArray items = inventarioDoc.getAsJsonArray("items");

                ObservableList<Inventario> filteredInventarioList = FXCollections.observableArrayList();
                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    String nombre = item.get("nombre").getAsString();
                    if (nombre.equalsIgnoreCase(nombreBuscado)) {
                        Inventario inventario = new Inventario(
                                item.get("id").getAsInt(),
                                item.get("nombre").getAsString(),
                                item.get("descripcion").getAsString(),
                                item.get("estado").getAsString(),
                                LocalDate.parse(item.get("fecha").getAsString()),
                                item.get("stock").getAsInt(),
                                item.get("precio").getAsDouble(),
                                item.get("categoria").getAsString()
                        );
                        filteredInventarioList.add(inventario);
                    }
                }
                tableInventario.setItems(filteredInventarioList);
            }
        } catch (CouchDbException e) {
            System.out.println("Error al filtrar el inventario: " + e.getMessage());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(cellData -> cellData.getValue().idInventarioProperty().asObject());
        Nombre.setCellValueFactory(cellData -> cellData.getValue().nombreInventarioProperty());
        Description.setCellValueFactory(cellData -> cellData.getValue().descripcionInventarioProperty());
        Estado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        Categoria.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());
        fecha.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(java.sql.Date.valueOf(cellData.getValue().getFechaInventario())));
        Stock.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        Precio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        cargarInventario();
        tableInventario.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doble clic
                Inventario selectedInventario = tableInventario.getSelectionModel().getSelectedItem();
                if (selectedInventario == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("No has seleccionado ningún inventario.");
                    alert.showAndWait();
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateProduct.fxml"));
                        Parent root = loader.load();
                        UpdateProductController updateProductController = loader.getController();
                        updateProductController.setInventario(selectedInventario);
                        updateProductController.setTableController(this);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Editar Producto");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("No se pudo abrir la ventana de edición.");
                        alert.setContentText("Detalles del error: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });










    }

    public void cargarInventario() {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();
        try {
            JsonObject inventarioDoc = dbClient.find(JsonObject.class, "Inventario");

            if (inventarioDoc != null && inventarioDoc.has("items")) {
                JsonArray items = inventarioDoc.getAsJsonArray("items");

                inventarioList.clear();
                ObservableList<String> nombreList = FXCollections.observableArrayList();

                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    Inventario inventario = new Inventario(
                            item.get("id").getAsInt(),
                            item.get("nombre").getAsString(),
                            item.get("descripcion").getAsString(),
                            item.get("estado").getAsString(),
                            LocalDate.parse(item.get("fecha").getAsString()),
                            item.get("stock").getAsInt(),
                            item.get("precio").getAsDouble(),
                            item.get("categoria").getAsString()
                    );
                    inventarioList.add(inventario);
                    nombreList.add(item.get("nombre").getAsString());
                }

                tableInventario.setItems(inventarioList);
                Nombre_Filtrar.setItems(nombreList);
            }
        } catch (CouchDbException e) {
            System.out.println("Error al cargar el inventario: " + e.getMessage());
        }
    }


    @FXML
    void onObtenerAction(ActionEvent event) {
        cargarInventario();
    }


    public Button getEliminar() {
        return Eliminar;
    }

    public void setEliminar(Button eliminar) {
        Eliminar = eliminar;
    }

    public Button getFiltrar() {
        return Filtrar;
    }

    public void setFiltrar(Button filtrar) {
        Filtrar = filtrar;
    }

    public TableColumn<Inventario, String> getDescription() {
        return Description;
    }

    public void setDescription(TableColumn<Inventario, String> description) {
        Description = description;
    }

    public TableColumn<Inventario, String> getEstado() {
        return Estado;
    }

    public void setEstado(TableColumn<Inventario, String> estado) {
        Estado = estado;
    }

    public TableColumn<Inventario, Date> getFecha() {
        return fecha;
    }

    public void setFecha(TableColumn<Inventario, Date> fecha) {
        this.fecha = fecha;
    }

    public TableColumn<Inventario, String> getNombre() {
        return Nombre;
    }

    public void setNombre(TableColumn<Inventario, String> nombre) {
        Nombre = nombre;
    }

    public TableColumn<Inventario, Integer> getId() {
        return id;
    }

    public void setId(TableColumn<Inventario, Integer> id) {
        this.id = id;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TableView<Inventario> getTableInventario() {
        return tableInventario;
    }

    public void setTableInventario(TableView<Inventario> tableInventario) {
        this.tableInventario = tableInventario;
    }

    public ComboBox<String> getNombre_Filtrar() {
        return Nombre_Filtrar;
    }

    public void setNombre_Filtrar(ComboBox<String> nombre_Filtrar) {
        Nombre_Filtrar = nombre_Filtrar;
    }

    public ObservableList<Inventario> getInventarioList() {
        return inventarioList;
    }

    public void setInventarioList(ObservableList<Inventario> inventarioList) {
        this.inventarioList = inventarioList;
    }
}
