package org.example.controlequipos.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized void iniciar() {
        if (emf == null || !emf.isOpen()) {
            String password = System.getenv("BD_PASSWORD");

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.password", password);
            try {
                emf = Persistence.createEntityManagerFactory("laboratorioPU", properties);
            } catch (Exception ex) {
                System.err.println("=== ERROR AL INICIALIZAR ENTITYMANAGERFACTORY ===");
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            iniciar();
        }
        return emf.createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}