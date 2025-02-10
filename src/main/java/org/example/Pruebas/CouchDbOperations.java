package org.example.Pruebas;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

public class CouchDbOperations {

    // Insertar un documento en la base de datos
    public static String insertDocument(String name, int age, String city) {
        CouchDbClient dbClient = CouchDbConnection.getClient();

        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("age", age);
        json.addProperty("city", city);

        Response response = dbClient.save(json);
        System.out.println("Documento guardado con ID: " + response.getId());
        return response.getId();
    }

    // Leer un documento por ID
    public static void getDocumentById(String id) {
        CouchDbClient dbClient = CouchDbConnection.getClient();

        try {
            JsonObject json = dbClient.find(JsonObject.class, id);
            System.out.println("Documento encontrado: " + json);
        } catch (Exception e) {
            System.out.println("Documento no encontrado");
        }
    }

    // Actualizar un documento por ID
    public static void updateDocument(String id, String newCity) {
        CouchDbClient dbClient = CouchDbConnection.getClient();

        try {
            JsonObject json = dbClient.find(JsonObject.class, id);
            json.addProperty("city", newCity);  // Modificamos el campo

            Response response = dbClient.update(json);
            System.out.println("Documento actualizado: " + response.getId());
        } catch (Exception e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }

    // Eliminar un documento por ID
    public static void deleteDocument(String id) {
        CouchDbClient dbClient = CouchDbConnection.getClient();

        try {
            JsonObject json = dbClient.find(JsonObject.class, id);
            Response response = dbClient.remove(json);
            System.out.println("Documento eliminado: " + response.getId());
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
}
