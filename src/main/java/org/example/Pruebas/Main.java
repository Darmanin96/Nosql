package org.example.Pruebas;

public class Main {
    public static void main(String[] args) {
        // Iniciar la conexión con CouchDB
        CouchDbConnection.initialize();

        // Insertar un documento
        String documentId = CouchDbOperations.insertDocument("Juan", 30, "Madrid");

        // Leer el documento
        CouchDbOperations.getDocumentById(documentId);

        // Actualizar el documento
        CouchDbOperations.updateDocument(documentId, "Barcelona");

        // Leer el documento después de actualizar
        CouchDbOperations.getDocumentById(documentId);


        // Cerrar la conexión
        CouchDbConnection.close();
    }
}
