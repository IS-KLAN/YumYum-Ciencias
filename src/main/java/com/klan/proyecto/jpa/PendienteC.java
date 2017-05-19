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
 * Controlador JPA para insertar, editar o borrar un Usuario en Pendiente de la
 * BD
 * @author patlani
 */
public class PendienteC implements Serializable {

    /**
     * Constructor de la clase JPA de Pendiente.
     * @param emf - objeto EntityManagerFactory para la conexión a la BD.
     */
    public PendienteC(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    /**
     * Objeto de EntityManagerFactory encargado de la conexión a la BD.
     */
    private EntityManagerFactory emf = null;

    /**
     * Método encargado de comenzar la conexión con la BD.
     * @return - Creación de un objeto para la conexión a la BD.
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Método encargado de crear un nuevo usuario pendiente en la BD
     * @param pendiente - objeto pendiente que se creará
     * @throws EntidadExistenteException - se lanza esta excepción cuando ya
     * existe un usuario con el mismo nombre en la BD
     * @throws Exception error ocurrido durante la creación del usuario.
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
     * Método para editar un pendiente existente en la BD
     * @param pendiente - datos del pendiente que se editará
     * @throws EntidadInexistenteException - excepción que se lanza cuando el
     * pendiente que se desea editar no existe en la BD.
     * @throws Exception - error ocurrido durante la edición del pendiente.
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
                    throw new EntidadInexistenteException(
                            "No existe pendiente con id " + id);
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
     * Método para eliminar un usuario  de la BD
     * @param nombre- nombre de usuario pendiente a eliminar
     * @throws EntidadInexistenteException - excepción que se lanza al no 
     * existir el uduario que se desea borrar de pendiente.
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
     * Método encargado de obtener un listado de todos los usuarios pendientes
     * que se encuentran almacenados en la base de datos.
     * @return - Lista de pendientes registrados en la base de datos.
     */
    public List<Pendiente> buscaPendientes() {
        return buscaPendientes(true, -1, -1);
    }

    /**
     * Método encargado de obtener un listado de todos los usuarios pendientes
     * que se encuentran almacenados en la base de datos.
     * @param resultadosMax - Indice hasta el que se obtienen los pendientes
     * @param primerResultado - Indice a partir del cual se obtienen los 
     * pendientes
     * @return - Lista de pendientes registrados en la base de datos.
     */
    public List<Pendiente> buscaPendientes(int resultadosMax, int primerResultado) {
        return buscaPendientes(false, resultadosMax, primerResultado);
    }

    /**
     * Método encargado de obtener un listado de todos los usuarios pendientes
     * que se encuentran almacenados en la base de datos.
     * @param todo - Indica si se obtienen todas las entidades
     * @param resultadosMax - Indice hasta el que se obtienen los pendientes
     * @param primerResultado - Indice a partir del cual se obtienen los 
     * usuarios pendientes
     * @return - Lista con los usuarios almacenadas en la BD
     */
    private List<Pendiente> buscaPendientes(boolean todo, 
                                                                                          int resultadosMax, int primerResultado) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pendiente.class));
            Query q = em.createQuery(cq);
            if (!todo) {
                q.setMaxResults(resultadosMax);
                q.setFirstResult(primerResultado);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Método que obtiene un usuario pendiente almacenado en la base de datos,
     * el cual es buscado a través de su nombre.
     * @param nombre - nombre del usuario pendiente a buscar.
     * @return Regresa el objeto pendiente encontrado.
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
     * Método que busca un usuario pendiente a través de su correo electrónico
     * @param correo - correo mediante el cual se realiza la busqueda.
     * @return Regresa el objeto pendiente encontrado.
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
     * Método encargado de obtener el número de usuarios pendientes
     * almacenados en la base de datos.
     * @return - el número de usuarios pendientes registrados.
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
