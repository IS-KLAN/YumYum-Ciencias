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
import javax.persistence.TypedQuery;
import org.primefaces.model.UploadedFile;

/**
 *Clase JPA para Crear, Modificar, y eliminar un puesto de la BD.
 *
 * @author patlani
 */
public class PuestoC implements Serializable {

    /**
     * Constructor de la clase JPA para un Puesto.
     * @param em  Objeto de EntityManagerFactory encargado de la conexión
     * a la BD.
     */
    public PuestoC(EntityManagerFactory em) {
    	this.emf = em;
    }

    /**
     * Objeto de EntityManagerFactory encargado de la conexión a la BD.
     */
    private EntityManagerFactory emf = null;

    /**
     * Método encargado de comenzar la conexión con la BD.
     * @return Objeto creado ara la conexión a la BD.
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Metodo encargado de la creacion de un puesto en la BD.
     * @param puesto	El puesto que se guardara en la BD
     * @throws EntidadExistenteException excepcion lanzada  cuando
     * ya existe un puesto guardado en la BD con el mismo nombre
     * @throws Exception Excepcion lanzanda si se presenta un
     * error al guardar el puesto en la base de datos.
     */
    public void crear(Puesto puesto) 
    					throws EntidadExistenteException, Exception {
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
                comida = em.getReference(comida.getClass(),
										comida.getNombre());
                comidas.add(comida);
            }
            puesto.setComidas(comidas);
            List<Evaluacion> evaluaciones = new ArrayList<Evaluacion>();
            for (Evaluacion evaluacion : puesto.getEvaluaciones()) {
                evaluacion = em.getReference(evaluacion.getClass(),
                							 evaluacion.getLlave());
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
                throw new EntidadExistenteException("Puesto " + puesto
                	+ " ya existe.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Metodo que se encarga de editar un puesto guardado en la BD.
     * @param puesto	El puesto a editar en la BD.
     * @throws InconsistenciasException	Excepcion lanzada al encontrar
     * datos que no corresponden con los anteriores.
     * @throws EntidadInexistenteException Excepcion lanzada al querer
     * editar un puesto que no existe en la BD
     * @throws Exception Excepcion lanzada al presentarse un error en la
     * edicion de un puesto de la BD.
     */
    public void editar(Puesto puesto)
                  throws InconsistenciasException, EntidadInexistenteException,
                  													Exception {
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
                    illegalOrphanMessages.add("Debe conservarse Evaluacion "
                    	+ evaluacion);
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
                evaluacion = em.getReference(evaluacion.getClass(),
                					evaluacion.getLlave());
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
                   throw new
                   EntidadInexistenteException("No existe puesto con id " + id);
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
     * Metodo que se encarga de borrar un Puesto de la BD a partir del
     * nombre del puesto.
     * @param nombre El nombre del puesto que se quiere borrar.
     * @throws InconsistenciasException	Excepcion lanzada al intentar borrar
     * un puesto sin haber eliminado las evaluaciones.
     * @throws EntidadInexistenteException Excepcion lanzada al intentar
     * borrar un puesto de la BD que no se encuentra guardada.
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
                throw new EntidadInexistenteException("No existe puesto con id "
                						+ nombre, enfe);
            }
            List<String> mensajes = null;
            List<Evaluacion> consistencias = puesto.getEvaluaciones();
            for (Evaluacion evaluacion : consistencias) {
                if (mensajes == null) {
                    mensajes = new ArrayList<String>();
                }
                mensajes.add("Puesto (" + puesto
                + ") no puede destruirse porque no existe Evaluacion "
                						+ evaluacion);
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
     * Metodo que recupera todos los puestos de la base de datos.
     * @return Una llamada al metodo buscaPuestos.
     */
    public List<Puesto> buscaPuestos() {
        return buscaPuestos(true, -1, -1);
    }

    /**
     * Metodo que recupera los puestos.
     * @param maxResults
     * @param firstResult
     * @return una llamada al metodo buscaPuestos.
     */
    public List<Puesto> buscaPuestos(int maxResults, int firstResult) {
        return buscaPuestos(false, maxResults, firstResult);
    }

    /**
     * Metodo que recupera la lista de todos los puestos registrados
     * en la BD
     * @param all
     * @param maxResults
     * @param firstResult
     * @return una lista con todos los puestos de la BD
     */
    private List<Puesto> buscaPuestos(boolean all, int maxResults,
    											   int firstResult) {
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
     * Metodo que realiza una busqueda entre los puestos por el nombre.
     * @param nombre El nombre del puesto que se quiere buscar.
     * @return El puesto con el nombre que le pasamos como parametro.
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
     * Metodo que realiza una busqueda entre los puestos por su ubicacion.
     * @param latitud La latitud que corresponde al puesto que necesitamos
     * buscar.
     * @param longitud La longitud que corresponde al puesto que necesitamos
     * buscar.
     * @return El puesto que se encuentra ubicado en la latitud y longitud
     * que pasamos como parametro.
     */
    public Puesto buscaLugar(String latitud, String longitud) {
        try {
            EntityManager em = getEntityManager();
            return (Puesto) (em.createNamedQuery("Puesto.buscaLugar")
                    .setParameter("latitud", latitud)
                    .setParameter("longitud", longitud).getSingleResult());
        } catch(Exception ex){
            System.err.println("Error al buscar el usuario con correo: "
                                                + latitud + ex.getMessage());
        } 
        return null;
    }  

    /**
     * Metodo que se encarga de contar todos los puestos registrados en la BD.
     * @return La cantidad de puestos que tenemos registrados en la BD.
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
    
    public boolean actualizaDescripcion(Puesto puesto, String descripcion){
               EntityManager em = null;
        try {
            em = getEntityManager();
            Puesto persistentPuesto = em.find(Puesto.class, puesto.getNombre());
            em.getTransaction().begin();
            persistentPuesto.setDescripcion(descripcion);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al actualizar descripcion"
                    + " " + ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return false;
    }
    
    public boolean actualizaImagen(Puesto puesto, String rutaImagen, byte[] archivo){
               EntityManager em = null;
        try {
            em = getEntityManager();
            Puesto persistentPuesto = em.find(Puesto.class, puesto.getNombre());
            em.getTransaction().begin();
            
            persistentPuesto.setRutaImagen(rutaImagen);
            persistentPuesto.setDatos(archivo);
            em.getTransaction().commit();
            System.out.println(" cargo en la base");
            
            return true;
        } catch (Exception ex) {
            System.err.println("Error al actualizar descripcion"
                    + " " + ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return false;
    }

    
}