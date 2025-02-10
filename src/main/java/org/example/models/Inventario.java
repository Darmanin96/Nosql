package org.example.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Inventario {
    private final IntegerProperty idInventario;
    private final StringProperty nombreInventario;
    private final StringProperty descripcionInventario;
    private final StringProperty estado;
    private final ObjectProperty<LocalDate> fechaInventario;
    private final IntegerProperty stock;
    private final DoubleProperty precio;
    private final StringProperty categoria;

    public Inventario(int id, String nombre, String descripcion, String estado, LocalDate fecha, int stock, double precio,String categoria) {
        this.idInventario = new SimpleIntegerProperty(id);
        this.nombreInventario = new SimpleStringProperty(nombre);
        this.descripcionInventario = new SimpleStringProperty(descripcion);
        this.estado = new SimpleStringProperty(estado);
        this.fechaInventario = new SimpleObjectProperty<>(fecha);
        this.stock = new SimpleIntegerProperty(stock);
        this.precio = new SimpleDoubleProperty(precio);
        this.categoria = new SimpleStringProperty(categoria);
    }


    public String getCategoria() {
        return categoria.get();
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

    public int getIdInventario() {
        return idInventario.get();
    }

    public IntegerProperty idInventarioProperty() {
        return idInventario;
    }

    public String getNombreInventario() {
        return nombreInventario.get();
    }

    public StringProperty nombreInventarioProperty() {
        return nombreInventario;
    }

    public String getDescripcionInventario() {
        return descripcionInventario.get();
    }

    public StringProperty descripcionInventarioProperty() {
        return descripcionInventario;
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public LocalDate getFechaInventario() {
        return fechaInventario.get();
    }

    public ObjectProperty<LocalDate> fechaInventarioProperty() {
        return fechaInventario;
    }

    public int getStock() {
        return stock.get();
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public double getPrecio() {
        return precio.get();
    }

    public DoubleProperty precioProperty() {
        return precio;
    }
}
