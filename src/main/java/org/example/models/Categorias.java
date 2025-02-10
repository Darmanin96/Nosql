package org.example.models;

import javafx.beans.property.*;

public class Categorias {
    private final StringProperty id;
    private final StringProperty nombre;
    private final StringProperty descripcion;


    public Categorias(String id, String nombre, String descripcion) {
        this.id = new SimpleStringProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }
}
