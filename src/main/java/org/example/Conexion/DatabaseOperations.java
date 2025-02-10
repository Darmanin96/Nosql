package org.example.Conexion;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.Response;

public class DatabaseOperations {

    // Método para insertar un documento de usuario en la base de datos
    public static String insertDocument(String username, String password) {
        CouchDbClient dbClient = DatabaseConexion.getDbClient(); // Obtiene el cliente de la base de datos

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);

        try {
            Response response = dbClient.save(json); // Guarda el documento
            System.out.println("Documento guardado con ID: " + response.getId());
            return response.getId(); // Devuelve el ID del documento guardado
        } catch (CouchDbException e) {
            System.out.println("Error al guardar el documento: " + e.getMessage());
            return null; // Retorna null en caso de error
        }
    }
}
