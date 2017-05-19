/*
 * To change license header, choose License Headers in Project Properties.
 * To change template file, choose Tools | Templates
 * and open No existe template in No existe editor.
 */
package com.klan.proyecto.jpa;

import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.EntidadExistenteException;
import com.klan.proyecto.modelo.Pendiente;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author patlani
 */
public class PendienteC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public PendienteC(EntityManagerFactory emf) {
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
     * @param pendiente
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Pendiente pendiente)
                  throws EntidadExistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(pendiente);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaNombre(pendiente.getNombre()) != null) {
                throw new EntidadExistenteException(
                                        "Pendiente " + pendiente + " ya existe.", ex);
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
     * @param pendiente
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Pendiente pendiente)
                  throws EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            pendiente = em.merge(pendiente);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pendiente.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException("No existe pendiente con id " + id);
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
            Pendiente pendiente;
            try {
                pendiente = em.getReference(Pendiente.class, nombre);
                pendiente.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException(
                                        "No existe pendiente con id " + nombre, enfe);
            }
            em.remove(pendiente);
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
    public List<Pendiente> buscaPendientes() {
        return buscaPendientes(true, -1, -1);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<Pendiente> buscaPendientes(int maxResults, int firstResult) {
        return buscaPendientes(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Pendiente> buscaPendientes(boolean all, 
                                                                                          int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pendiente.class));
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
    public Pendiente buscaNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pendiente.class, nombre);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @param correo
     * @return 
     */
    public Pendiente buscaCorreo(String correo) {
        try{
            EntityManager em = getEntityManager();
            return (Pendiente)(em.createNamedQuery("Pendiente.buscaCorreo")
                    .setParameter("correo", correo).getSingleResult());
        }catch(Exception ex){
            System.err.println("Error al buscar el usuario con correo: "
                                                + correo + ex.getMessage());
        } return null;
    }    

    /**
     * 
     * @return 
     */
    public int cantidadDePendientes() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pendiente> rt = cq.from(Pendiente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
