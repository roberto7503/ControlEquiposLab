package org.example.controlequipos.dao;

import jakarta.persistence.EntityManager;
import org.example.controlequipos.database.JPAUtil;
import org.example.controlequipos.model.TipoEquipo;

import java.util.List;

public class TipoEquipoDAO {

    public List<TipoEquipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoEquipo t ORDER BY t.nombre", TipoEquipo.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public TipoEquipo buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(TipoEquipo.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}