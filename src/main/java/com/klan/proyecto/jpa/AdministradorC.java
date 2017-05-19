/*
 * To change license header, choose License Headers in Project Properties.
 * To change template file, choose Tools | Templates
 * and open No existe template in No existe editor.
 */
package com.klan.proyecto.jpa;

import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.EntidadExistenteException;
import com.klan.proyecto.modelo.Administrador;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Controlador JPA para insertar, editar o borrar administradores de la BD.
 * @author patlani
 */
public class AdministradorC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public AdministradorC(EntityManagerFactory emf) {
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
     * @param administrador
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Administrador administrador)
                  throws EntidadExistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(administrador);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaNombre(administrador.getNombre()) != null) {
                throw new EntidadExistenteException( "Administrador "
                + administrador + " ya existe.", ex);
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
     * @param administrador
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Administrador administrador)
                  throws EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            administrador = em.merge(administrador);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = administrador.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException(
                            "No existe administrador con id " + id);
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
            Administrador administrador;
            try {
                administrador = em.getReference(Administrador.class, nombre);
                administrador.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException(
                        "No existe administrador con id " + nombre, enfe);
            }
            em.remove(administrador);
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
    public List<Administrador> buscaAdministradores() {
        return buscaAdministradores(true, -1, -1);
    }

    public List<Administrador> buscaAdministradores(int maxResults,
            int firstResult) {
        return buscaAdministradores(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Administrador> buscaAdministradores(boolean all,
        int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrador.class));
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
    public Administrador buscaNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Administrador.class, nombre);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @return 
     */
    public int cantidadDeAdministradores() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Administrador> rt = cq.from(Administrador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
