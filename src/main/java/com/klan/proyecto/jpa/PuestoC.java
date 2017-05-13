/*
 * To change license header, choose License Headers in Project Properties.
 * To change template file, choose Tools | Templates
 * and open No existe template in No existe editor.
 */
package com.klan.proyecto.jpa;

import com.klan.proyecto.jpa.exceptions.InconsistenciasException;
import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.EntidadExistenteException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.klan.proyecto.modelo.Comida;
import java.util.ArrayList;
import java.util.List;
import com.klan.proyecto.modelo.Evaluacion;
import com.klan.proyecto.modelo.Puesto;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author patlani
 */
public class PuestoC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public PuestoC(EntityManagerFactory emf) {
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
     * @param puesto
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Puesto puesto) throws EntidadExistenteException, Exception {
        if (puesto.getComidas() == null) {
            puesto.setComidas(new ArrayList<Comida>());
        }
        if (puesto.getEvaluaciones() == null) {
            puesto.setEvaluaciones(new ArrayList<Evaluacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Comida> comidas = new ArrayList<Comida>();
            for (Comida comida : puesto.getComidas()) {
                comida = em.getReference(comida.getClass(), comida.getNombre());
                comidas.add(comida);
            }
            puesto.setComidas(comidas);
            List<Evaluacion> evaluaciones = new ArrayList<Evaluacion>();
            for (Evaluacion evaluacion : puesto.getEvaluaciones()) {
                evaluacion = em.getReference(evaluacion.getClass(), evaluacion.getLlave());
                evaluaciones.add(evaluacion);
            }
            puesto.setEvaluaciones(evaluaciones);
            em.persist(puesto);
            for (Comida comidasComida : puesto.getComidas()) {
                comidasComida.getPuestos().add(puesto);
                comidasComida = em.merge(comidasComida);
            }
            for (Evaluacion evaluacion : puesto.getEvaluaciones()) {
                Puesto original = evaluacion.getPuesto();
                evaluacion.setPuesto(puesto);
                evaluacion = em.merge(evaluacion);
                if (original != null) {
                    original.getEvaluaciones().remove(evaluacion);
                    original = em.merge(original);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaNombre(puesto.getNombre()) != null) {
                throw new EntidadExistenteException("Puesto " + puesto + " ya existe.", ex);
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
     * @param puesto
     * @throws InconsistenciasException
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Puesto puesto)
                  throws InconsistenciasException, EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Puesto persistentPuesto = em.find(Puesto.class, puesto.getNombre());
            List<Comida> comidasOriginales = persistentPuesto.getComidas();
            List<Comida> comidasNuevas = puesto.getComidas();
            List<Evaluacion> evaluacionesOld = persistentPuesto.getEvaluaciones();
            List<Evaluacion> evaluacionesNew = puesto.getEvaluaciones();
            List<String> illegalOrphanMessages = null;
            for (Evaluacion evaluacion : evaluacionesOld) {
                if (!evaluacionesNew.contains(evaluacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("Debe conservarse Evaluacion " + evaluacion);
                }
            }
            if (illegalOrphanMessages != null) {
                throw new InconsistenciasException(illegalOrphanMessages);
            }
            List<Comida> attachedComidasNew = new ArrayList<Comida>();
            for (Comida comida : comidasNuevas) {
                comida = em.getReference(comida.getClass(), comida.getNombre());
                attachedComidasNew.add(comida);
            }
            comidasNuevas = attachedComidasNew;
            puesto.setComidas(comidasNuevas);
            List<Evaluacion> evaluacionesNuevas = new ArrayList<Evaluacion>();
            for (Evaluacion evaluacion : evaluacionesNew) {
                evaluacion = em.getReference(evaluacion.getClass(), evaluacion.getLlave());
                evaluacionesNuevas.add(evaluacion);
            }
            evaluacionesNew = evaluacionesNuevas;
            puesto.setEvaluaciones(evaluacionesNew);
            puesto = em.merge(puesto);
            for (Comida comida : comidasOriginales) {
                if (!comidasNuevas.contains(comida)) {
                    comida.getPuestos().remove(puesto);
                    comida = em.merge(comida);
                }
            }
            for (Comida comida : comidasNuevas) {
                if (!comidasOriginales.contains(comida)) {
                    comida.getPuestos().add(puesto);
                    comida = em.merge(comida);
                }
            }
            for (Evaluacion evaluacion : evaluacionesNew) {
                if (!evaluacionesOld.contains(evaluacion)) {
                    Puesto original = evaluacion.getPuesto();
                    evaluacion.setPuesto(puesto);
                    evaluacion = em.merge(evaluacion);
                    if (original != null && !original.equals(puesto)) {
                        original.getEvaluaciones().remove(evaluacion);
                        original = em.merge(original);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = puesto.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException("No existe puesto con id " + id);
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
     * @throws InconsistenciasException
     * @throws EntidadInexistenteException 
     */
    public void borrar(String nombre)
                  throws InconsistenciasException, EntidadInexistenteException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Puesto puesto;
            try {
                puesto = em.getReference(Puesto.class, nombre);
                puesto.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException("No existe puesto con id " + nombre, enfe);
            }
            List<String> mensajes = null;
            List<Evaluacion> consistencias = puesto.getEvaluaciones();
            for (Evaluacion evaluacion : consistencias) {
                if (mensajes == null) {
                    mensajes = new ArrayList<String>();
                }
                mensajes.add("Puesto (" + puesto
                + ") no puede destruirse porque no existe Evaluacion " + evaluacion);
            }
            if (mensajes != null) {
                throw new InconsistenciasException(mensajes);
            }
            List<Comida> comidas = puesto.getComidas();
            for (Comida comida : comidas) {
                comida.getPuestos().remove(puesto);
                comida = em.merge(comida);
            }
            em.remove(puesto);
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
    public List<Puesto> buscaPuestos() {
        return buscaPuestos(true, -1, -1);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<Puesto> buscaPuestos(int maxResults, int firstResult) {
        return buscaPuestos(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Puesto> buscaPuestos(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Puesto.class));
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
    public Puesto buscaNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Puesto.class, nombre);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @param latitud
     * @param longitud
     * @return 
     */
    public Puesto buscaLugar(String latitud, String longitud) {
        try{
            EntityManager em = getEntityManager();
            return (Puesto)(em.createNamedQuery("Puesto.buscaLugar")
                    .setParameter("latitud", latitud)
                    .setParameter("longitud", longitud).getSingleResult());
        }catch(Exception ex){
            System.err.println("Error al buscar el usuario con correo: "
                                                + latitud + ex.getMessage());
        } return null;
    }    

    /**
     * 
     * @return 
     */
    public int cantidadDePuestos() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Puesto> rt = cq.from(Puesto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
