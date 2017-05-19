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
import com.klan.proyecto.modelo.Evaluacion;
import com.klan.proyecto.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author patlani
 */
public class UsuarioC implements Serializable {

    /**
     * 
     * @param emf 
     */
    public UsuarioC(EntityManagerFactory emf) {
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
     * @param usuario
     * @throws EntidadExistenteException
     * @throws Exception 
     */
    public void crear(Usuario usuario) throws EntidadExistenteException, Exception {
        if (usuario.getEvaluaciones() == null) {
            usuario.setEvaluaciones(new ArrayList<Evaluacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Evaluacion> attachedEvaluaciones = new ArrayList<Evaluacion>();
            for (Evaluacion evaluacion : usuario.getEvaluaciones()) {
                evaluacion = em.getReference(evaluacion.getClass(), evaluacion.getLlave());
                attachedEvaluaciones.add(evaluacion);
            }
            usuario.setEvaluaciones(attachedEvaluaciones);
            em.persist(usuario);
            for (Evaluacion evaluacionesEvaluacion : usuario.getEvaluaciones()) {
                Usuario original = evaluacionesEvaluacion.getUsuario();
                evaluacionesEvaluacion.setUsuario(usuario);
                evaluacionesEvaluacion = em.merge(evaluacionesEvaluacion);
                if (original != null) {
                    original.getEvaluaciones().remove(evaluacionesEvaluacion);
                    original = em.merge(original);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (buscaNombre(usuario.getNombre()) != null) {
                throw new EntidadExistenteException("Usuario " + usuario + " ya existe.", ex);
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
     * @param usuario
     * @throws InconsistenciasException
     * @throws EntidadInexistenteException
     * @throws Exception 
     */
    public void editar(Usuario usuario)
                  throws InconsistenciasException, EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNombre());
            List<Evaluacion> originales = persistentUsuario.getEvaluaciones();
            List<Evaluacion> nuevas = usuario.getEvaluaciones();
            List<String> mensaje = null;
            for (Evaluacion original : originales) {
                if (!nuevas.contains(original)) {
                    if (mensaje == null) {
                        mensaje = new ArrayList<String>();
                    }
                    mensaje.add("Debe conservarse Evaluacion "
                                                + original + " desde que se inicializa.");
                }
            }
            if (mensaje != null) {
                throw new InconsistenciasException(mensaje);
            }
            List<Evaluacion> attachedEvaluacionesNew = new ArrayList<Evaluacion>();
            for (Evaluacion nueva : nuevas) {
                nueva = em.getReference(nueva.getClass(), nueva.getLlave());
                attachedEvaluacionesNew.add(nueva);
            }
            nuevas = attachedEvaluacionesNew;
            usuario.setEvaluaciones(nuevas);
            usuario = em.merge(usuario);
            for (Evaluacion nueva : nuevas) {
                if (!originales.contains(nueva)) {
                    Usuario original = nueva.getUsuario();
                    nueva.setUsuario(usuario);
                    nueva = em.merge(nueva);
                    if (original != null && !original.equals(usuario)) {
                        original.getEvaluaciones().remove(nueva);
                        original = em.merge(original);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException("No existe usuario con id " + id);
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, nombre);
                usuario.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException("No existe usuario con id " + nombre, enfe);
            }
            List<String> mensaje = null;
            List<Evaluacion> consistencias = usuario.getEvaluaciones();
            for (Evaluacion evaluacion : consistencias) {
                if (mensaje == null) {
                    mensaje = new ArrayList<String>();
                }
                mensaje.add("Usuario inconsistente (" + usuario + ")");
            }
            if (mensaje != null) {
                throw new InconsistenciasException(mensaje);
            }
            em.remove(usuario);
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
    public List<Usuario> buscaUsuarios() {
        return buscaUsuarios(true, -1, -1);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<Usuario> buscaUsuarios(int maxResults, int firstResult) {
        return buscaUsuarios(false, maxResults, firstResult);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @return 
     */
    private List<Usuario> buscaUsuarios(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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
    public Usuario buscaNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, nombre);
        } finally {
            em.close();
        }
    }

    /**
     * 
     * @param correo
     * @return 
     */
    public Usuario buscaCorreo(String correo) {
        try{
            EntityManager em = getEntityManager();
            return (Usuario)(em.createNamedQuery("Usuario.buscaCorreo")
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
    public int cantidadDeUsuarios() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
