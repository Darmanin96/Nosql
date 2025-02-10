package org.example.controllers;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class RootViewController implements Initializable {


    @FXML
    private Tab categorias;

    @FXML
    private Tab inventario;

    @FXML
    private BorderPane root;

    @FXML
    private TabPane tabVista;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public RootViewController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RootView.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Tab getCategorias() {
        return categorias;
    }

    public void setCategorias(Tab categorias) {
        this.categorias = categorias;
    }

    public Tab getInventario() {
        return inventario;
    }

    public void setInventario(Tab inventario) {
        this.inventario = inventario;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TabPane getTabVista() {
        return tabVista;
    }

    public void setTabVista(TabPane tabVista) {
        this.tabVista = tabVista;
    }
}
