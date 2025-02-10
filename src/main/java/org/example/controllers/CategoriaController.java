package org.example.controllers;

import com.google.gson.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import org.example.Conexion.*;
import org.example.models.*;
import org.lightcouch.*;

import java.net.*;
import java.time.*;
import java.util.*;

public class CategoriaController implements Initializable {


    @FXML
    private TableColumn<Categorias, String> description;

    @FXML
    private TableColumn<Categorias, String> id;

    @FXML
    private TableColumn<Categorias, String> nombre;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Categorias> tableCategoria;

    private ObservableList<Categorias> CategoriaList = FXCollections.observableArrayList();




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        description.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        tableCategoria.setItems(CategoriaList);
        cargarCategorias();

    }



    public void cargarCategorias() {
        CouchDbClient dbClient = DatabaseConexion.getDbClient();
        try {
            JsonObject categoriaDoc = dbClient.find(JsonObject.class, "Categoria");

            if (categoriaDoc != null) {
                System.out.println("Documento encontrado: " + categoriaDoc);  // Verifica si los datos existen

                if (categoriaDoc.has("categorias")) {
                    JsonArray categoriasArray = categoriaDoc.getAsJsonArray("categorias");
                    System.out.println("Cantidad de categorías encontradas: " + categoriasArray.size());  // Debug

                    CategoriaList.clear();

                    for (JsonElement elemento : categoriasArray) {
                        JsonObject item = elemento.getAsJsonObject();
                        System.out.println("Procesando categoría: " + item);  // Verifica cada elemento

                        Categorias categoria = new Categorias(
                                item.get("id").getAsString(),
                                item.get("nombre").getAsString(),
                                item.get("descripcion").getAsString()
                        );
                        CategoriaList.add(categoria);
                    }

                    tableCategoria.setItems(CategoriaList);  // Cargar los datos en la tabla
                    System.out.println("Datos cargados en la tabla: " + CategoriaList.size());  // Verifica si se agregan datos
                } else {
                    System.out.println("El documento no tiene la clave 'categorias'.");
                }
            } else {
                System.out.println("No se encontró el documento en la base de datos.");
            }
        } catch (CouchDbException e) {
            System.out.println("Error al cargar las categorías: " + e.getMessage());
        }
    }


    @FXML
    void onAñadirAction(ActionEvent event) {



    }

    @FXML
    void onEliminarAction(ActionEvent event) {
        Categorias tableSeleccionado = tableCategoria.getSelectionModel().getSelectedItem();
        if (tableSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No has seleccionado ningún elemento");
            alert.setContentText("Por favor, debes seleccionar un elemento.");
            alert.showAndWait();
        }else {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este elemento?");
            confirmacion.setContentText("Nombre: " + tableSeleccionado.getNombre());
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    CouchDbClient dbClient = DatabaseConexion.getDbClient();
                    JsonObject doc = dbClient.find(JsonObject.class, "Categoria");

                    if (doc != null) {
                        JsonArray categoriasArray = doc.getAsJsonArray("categorias");
                        JsonArray newCategoriasArray = new JsonArray();
                        for (JsonElement categoriaElement : categoriasArray) {
                            JsonObject categoriaObject = categoriaElement.getAsJsonObject();
                            if (!categoriaObject.get("id").getAsString().equals(tableSeleccionado.getId())) {
                                newCategoriasArray.add(categoriaObject);
                            }
                        }
                        JsonObject updatedDoc = new JsonObject();
                        updatedDoc.addProperty("_id", doc.get("_id").getAsString());
                        updatedDoc.addProperty("_rev", doc.get("_rev").getAsString());
                        updatedDoc.add("categorias", newCategoriasArray);
                        dbClient.update(updatedDoc);
                        tableCategoria.getItems().remove(tableSeleccionado);
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Eliminado");
                        successAlert.setHeaderText("La categoría ha sido eliminada correctamente.");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("No se encontró el documento de Categoría.");
                        errorAlert.showAndWait();
                    }
                } catch (CouchDbException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Hubo un problema al eliminar la categoría.");
                    errorAlert.setContentText("Mensaje del error: " + e.getMessage());
                    errorAlert.showAndWait();
                }

            }
        }


    }




    public TableColumn<Categorias, String> getDescription() {
        return description;
    }

    public void setDescription(TableColumn<Categorias, String> description) {
        this.description = description;
    }

    public TableColumn<Categorias, String> getId() {
        return id;
    }

    public void setId(TableColumn<Categorias, String> id) {
        this.id = id;
    }

    public TableColumn<Categorias, String> getNombre() {
        return nombre;
    }

    public void setNombre(TableColumn<Categorias, String> nombre) {
        this.nombre = nombre;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TableView<Categorias> getTableCategoria() {
        return tableCategoria;
    }

    public void setTableCategoria(TableView<Categorias> tableCategoria) {
        this.tableCategoria = tableCategoria;
    }

    public ObservableList<Categorias> getCategoriaList() {
        return CategoriaList;
    }

    public void setCategoriaList(ObservableList<Categorias> categoriaList) {
        CategoriaList = categoriaList;
    }
}
