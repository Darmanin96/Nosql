package org.example.Conexion;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class DatabaseConexion {

    private static CouchDbClient dbClient;

    public static CouchDbClient getDbClient() {
        if (dbClient == null) {
            iniciarConexion();
        }
        return dbClient;
    }

    public static void iniciarConexion() {
        if (dbClient == null) {
            CouchDbProperties properties = new CouchDbProperties()
                    .setCreateDbIfNotExist(true)
                    .setDbName("usuarios")
                    .setProtocol("http")
                    .setHost("localhost")
                    .setPort(5984)
                    .setUsername("Admin")
                    .setPassword("Machomen6")
                    .setMaxConnections(100);
            dbClient = new CouchDbClient(properties);
        }
    }
}
