package org.example.Pruebas;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class CouchDbConnection {
    private static CouchDbClient dbClient;

    public static void initialize() {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("testdb")  // Nombre de la base de datos
                .setCreateDbIfNotExist(true)  // Crear si no existe
                .setProtocol("http")
                .setHost("localhost")
                .setPort(5984)
                .setUsername("Admin")  // Cambia según tu configuración
                .setPassword("Machomen6")  // Cambia según tu configuración
                .setMaxConnections(100);

        dbClient = new CouchDbClient(properties);
    }

    public static CouchDbClient getClient() {
        return dbClient;
    }

    public static void close() {
        dbClient.shutdown();
    }
}
