/*
 * To change license header, choose License Headers in Project Properties.
 * To change template file, choose Tools | Templates
 * and open No existe template in No existe editor.
 */
package com.klan.proyecto.jpa;

import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.EntidadExistenteException;
import com.klan.proyecto.modelo.Comida;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.klan.proyecto.modelo.Puesto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author patlani
 */
public class ComidaC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public ComidaC(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    /**
     * 
     */
    private EntityManagerFactory emf = null;

    /**
     * 
     * @return 
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * 
     * @param comida
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Comida comida) throws EntidadExistenteException, Exception {
        if (comida.getPuestos() == null) {
            comida.setPuestos(new ArrayList<Puesto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Puesto> puestos = new ArrayList<Puesto>();
            for (Puesto puesto : comida.getPuestos()) {
                puesto = em.getReference(puesto.getClass(), puesto.getNombre());
                puestos.add(puesto);
            }
            comida.setPuestos(puestos);
            em.persist(comida);
            for (Puesto PuestosPuesto : comida.getPuestos()) {
                PuestosPuesto.getComidas().add(comida);
                PuestosPuesto = em.merge(PuestosPuesto);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaNombre(comida.getNombre()) != null) {
                throw new EntidadExistenteException("Comida " + comida + " ya existe.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * 
     * @param comida
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Comida comida) throws EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comida persistentComida = em.find(Comida.class, comida.getNombre());
            List<Puesto> originales = persistentComida.getPuestos();
            List<Puesto> nuevos = comida.getPuestos();
            List<Puesto> puestos = new ArrayList<Puesto>();
            for (Puesto nuevo : nuevos) {
                nuevo = em.getReference(nuevo.getClass(), nuevo.getNombre());
                puestos.add(nuevo);
            }
            nuevos = puestos;
            comida.setPuestos(nuevos);
            comida = em.merge(comida);
            for (Puesto original : originales) {
                if (!nuevos.contains(original)) {
                    original.getComidas().remove(comida);
                    original = em.merge(original);
                }
            }
            for (Puesto nuevo : nuevos) {
                if (!originales.contains(nuevo)) {
                    nuevo.getComidas().add(comida);
                    nuevo = em.merge(nuevo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = comida.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException("No existe comida con id " + id);
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * 
     * @param nombre
     * @throws EntidadInexistenteException 
     */
    public void borrar(String nombre) throws EntidadInexistenteException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comida comida;
            try {
                comida = em.getReference(Comida.class, nombre);
                comida.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException("No existe comida con id " + nombre, enfe);
            }
            List<Puesto> Puestos = comida.getPuestos();
            for (Puesto PuestosPuesto : Puestos) {
                PuestosPuesto.getComidas().remove(comida);
                PuestosPuesto = em.merge(PuestosPuesto);
            }
            em.remove(comida);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * 
     * @return 
     */
    public List<Comida> buscaComidas() {
        return buscaComidas(true, -1, -1);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<Comida> buscaComidas(int maxResults, int firstResult) {
        return buscaComidas(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Comida> buscaComidas(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comida.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @param nombre
     * @return 
     */
    public Comida buscaNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comida.class, nombre);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @return 
     */
    public int cantidadDeComidas() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comida> rt = cq.from(Comida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
