package org.example.controlequipos.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.example.controlequipos.database.JPAUtil;
import org.example.controlequipos.model.Equipo;
import org.example.controlequipos.model.TipoEquipo;

import java.util.List;

public class EquipoDAO {

    private TipoEquipo obtenerTipoEquipo(EntityManager em, Equipo equipo) {
        if (equipo == null || equipo.getTipoEquipo() == null || equipo.getTipoEquipo().getId() == null) {
            throw new IllegalArgumentException("El equipo debe contener un tipo con un ID válido.");
        }
        Long tipoId = equipo.getTipoEquipo().getId();
        TipoEquipo tipo = em.find(TipoEquipo.class, tipoId);

        if (tipo == null) {
            throw new IllegalArgumentException("No se encontró el tipo de equipo con ID: " + tipoId);
        }
        return tipo;
    }

    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            TipoEquipo tipoGestionado = obtenerTipoEquipo(em, equipo);
            equipo.setTipoEquipo(tipoGestionado);

            em.persist(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo ORDER BY e.id", Equipo.class)
                    .getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Equipo buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Equipo> resultados = em.createQuery(
                            "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE e.id = :id", Equipo.class)
                    .setParameter("id", id)
                    .getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean modificar(Equipo equipo) {
        if (equipo == null || equipo.getId() == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, equipo.getId());

            if (equipoBD == null) {
                tx.rollback();
                return false;
            }

            equipoBD.setCodigo(equipo.getCodigo());
            equipoBD.setNombre(equipo.getNombre());
            equipoBD.setMarca(equipo.getMarca());
            equipoBD.setEstado(equipo.getEstado());
            equipoBD.setCantidad(equipo.getCantidad());

            if (equipo.getTipoEquipo() != null && equipo.getTipoEquipo().getId() != null) {
                TipoEquipo tipoGestionado = obtenerTipoEquipo(em, equipo);
                equipoBD.setTipoEquipo(tipoGestionado);
            }

            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, id);
            if (equipoBD == null) {
                tx.rollback();
                return false;
            }
            em.remove(equipoBD);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // Búsqueda por fragmento de nombre y filtro por tipo mediante JPQL con setParameter()
    public List<Equipo> buscarYFiltrar(String nombreParcial, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE 1=1 ");

            boolean tieneNombre = nombreParcial != null && !nombreParcial.trim().isEmpty();
            boolean tieneTipo = tipo != null && tipo.getId() != null;

            if (tieneNombre) {
                jpql.append("AND LOWER(e.nombre) LIKE LOWER(:nombre) ");
            }
            if (tieneTipo) {
                jpql.append("AND e.tipoEquipo.id = :tipoId ");
            }
            jpql.append("ORDER BY e.id");

            TypedQuery<Equipo> query = em.createQuery(jpql.toString(), Equipo.class);

            if (tieneNombre) {
                query.setParameter("nombre", "%" + nombreParcial.trim() + "%");
            }
            if (tieneTipo) {
                query.setParameter("tipoId", tipo.getId());
            }

            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}