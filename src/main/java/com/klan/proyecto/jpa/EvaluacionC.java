/*
 * To change license header, choose License Headers in Project Properties.
 * To change template file, choose Tools | Templates
 * and open No existe template in No existe editor.
 */
package com.klan.proyecto.jpa;

import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.EntidadExistenteException;
import com.klan.proyecto.modelo.Evaluacion;
import com.klan.proyecto.modelo.EvaluacionP;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.klan.proyecto.modelo.Puesto;
import com.klan.proyecto.modelo.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author patlani
 */
public class EvaluacionC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public EvaluacionC(EntityManagerFactory emf) {
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
     * @param evaluacion
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Evaluacion evaluacion)
                  throws EntidadExistenteException, Exception {
        if (evaluacion.getLlave() == null) {
            evaluacion.setLlave(new EvaluacionP());
        }
        evaluacion.getLlave().setNombreUsuario(evaluacion.getUsuario().getNombre());
        evaluacion.getLlave().setNombrePuesto(evaluacion.getPuesto().getNombre());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Puesto puesto = evaluacion.getPuesto();
            if (puesto != null) {
                puesto = em.getReference(puesto.getClass(), puesto.getNombre());
                evaluacion.setPuesto(puesto);
            }
            Usuario usuario = evaluacion.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getNombre());
                evaluacion.setUsuario(usuario);
            }
            em.persist(evaluacion);
            if (puesto != null) {
                puesto.getEvaluaciones().add(evaluacion);
                puesto = em.merge(puesto);
            }
            if (usuario != null) {
                usuario.getEvaluaciones().add(evaluacion);
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaEvaluacion(evaluacion.getLlave()) != null) {
                throw new EntidadExistenteException(
                                        "Evaluacion " + evaluacion + " ya existe.", ex);
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
     * @param evaluacion
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Evaluacion evaluacion)
                  throws EntidadInexistenteException, Exception {
        evaluacion.getLlave().setNombreUsuario(evaluacion.getUsuario().getNombre());
        evaluacion.getLlave().setNombrePuesto(evaluacion.getPuesto().getNombre());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Evaluacion persistentEvaluacion = em.find(Evaluacion.class, 
                                                                                                evaluacion.getLlave());
            Puesto original = persistentEvaluacion.getPuesto();
            Puesto nuevo = evaluacion.getPuesto();
            Usuario usuarioOriginal = persistentEvaluacion.getUsuario();
            Usuario usuarioNuevo = evaluacion.getUsuario();
            if (nuevo != null) {
                nuevo = em.getReference(nuevo.getClass(), nuevo.getNombre());
                evaluacion.setPuesto(nuevo);
            }
            if (usuarioNuevo != null) {
                usuarioNuevo = em.getReference(usuarioNuevo.getClass(), 
                                                                                    usuarioNuevo.getNombre());
                evaluacion.setUsuario(usuarioNuevo);
            }
            evaluacion = em.merge(evaluacion);
            if (original != null && !original.equals(nuevo)) {
                original.getEvaluaciones().remove(evaluacion);
                original = em.merge(original);
            }
            if (nuevo != null && !nuevo.equals(original)) {
                nuevo.getEvaluaciones().add(evaluacion);
                nuevo = em.merge(nuevo);
            }
            if (usuarioOriginal != null && !usuarioOriginal.equals(usuarioNuevo)) {
                usuarioOriginal.getEvaluaciones().remove(evaluacion);
                usuarioOriginal = em.merge(usuarioOriginal);
            }
            if (usuarioNuevo != null && !usuarioNuevo.equals(usuarioOriginal)) {
                usuarioNuevo.getEvaluaciones().add(evaluacion);
                usuarioNuevo = em.merge(usuarioNuevo);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                EvaluacionP id = evaluacion.getLlave();
                if (buscaEvaluacion(id) == null) {
                    throw new EntidadInexistenteException("No existe evaluacion con id " + id);
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
     * @param id
     * @throws EntidadInexistenteException 
     */
    public void borrar(EvaluacionP id) throws EntidadInexistenteException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Evaluacion evaluacion;
            try {
                evaluacion = em.getReference(Evaluacion.class, id);
                evaluacion.getLlave();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException(
                                        "No existe evaluacion con id " + id, enfe);
            }
            Puesto puesto = evaluacion.getPuesto();
            if (puesto != null) {
                puesto.getEvaluaciones().remove(evaluacion);
                puesto = em.merge(puesto);
            }
            Usuario usuario = evaluacion.getUsuario();
            if (usuario != null) {
                usuario.getEvaluaciones().remove(evaluacion);
                usuario = em.merge(usuario);
            }
            em.remove(evaluacion);
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
    public List<Evaluacion> buscaEvaluacions() {
        return buscaEvaluacions(true, -1, -1);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<Evaluacion> buscaEvaluacions(int maxResults, int firstResult) {
        return buscaEvaluacions(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Evaluacion> buscaEvaluacions(boolean all,
                                                                                            int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Evaluacion.class));
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
     * @param llave
     * @return 
     */
    public Evaluacion buscaEvaluacion(EvaluacionP llave) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Evaluacion.class, llave);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @return 
     */
    public int cantidadDeEvaluacions() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Evaluacion> rt = cq.from(Evaluacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
