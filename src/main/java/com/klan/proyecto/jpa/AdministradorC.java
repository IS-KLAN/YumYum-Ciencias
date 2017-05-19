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
     * Constructor de la clase JPA de Administrador.
     * @param emf - objeto EntityManagerFactory para la conexión a la BD.
     */
    public AdministradorC(EntityManagerFactory emf) {
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
     * Método encargado de la creación de un administror al sistema.
     * @param administrador - objeto administrador que se creará.
     * @throws EntidadExistenteException - excepción lanzada cuando existen
     * dos administradores con nombres duplicados.
     * @throws Exception - error ocurrido durante la creación de administrador.
     */
    public void crear(Administrador administrador)
                throws EntidadExistenteException, Exception {
        EntityManager em = null;
        try {
            // Comenzamos la transacción de inserción en la BD.
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(administrador);
            em.getTransaction().commit();
        } catch (Exception ex) {
            // Si existen dos administradores con nombres iguales, envíamos un
            // error, puesto que los valores son duplicados.
            if (buscaNombre(administrador.getNombre()) != null) {
                throw new EntidadExistenteException("Administrador "
                + administrador + " ya existe.", ex);
            }
            // Monstramos cualquier otro error ocurrido durante la transacción.
            throw ex;
        } finally {
            if (em != null) {
                // Cerramos la conexión a la BD.
                em.close();
            }
        }
    }

    /**
     * Método que se encarga de editar los datos de un administror.
     * @param administrador - objeto administrador que se va a editar.
     * @throws EntidadInexistenteException - en caso de no existir tal
     * administrador a editar, se lanza un error.
     * @throws Exception- error ocurrido durante la creación de administrador.
     */
    public void editar(Administrador administrador)
                throws EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            //Comenzamos la transacción de la modificación del
            // administrador en la BD.
            em = getEntityManager();
            em.getTransaction().begin();
            administrador = em.merge(administrador);
            em.getTransaction().commit();
        } catch (Exception ex) {
            //En caso de que no exista el administrador, se lanza un error.
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = administrador.getNombre();
                if (buscaNombre(id) == null) {
                    throw new EntidadInexistenteException(
                            "No existe administrador con id " + id);
                }
            }
            // Monstramos cualquier otro error ocurrido durante la transacción.
            throw ex;
        } finally {
            if (em != null) {
                // Cerramos la conexión a la BD.
                em.close();
            }
        }
    }

    /**
     * Método que se encarga de eliminar un administror.
     * @param nombre - nombre del administrador a borrar.
     * @throws EntidadInexistenteException - en caso de no existir tal
     * administrador a eliminar, se lanza un error.
     */
    public void borrar(String nombre) throws EntidadInexistenteException {
        EntityManager em = null;
        try {
            // Comenzamos la transacción en la BD.
            em = getEntityManager();
            em.getTransaction().begin();
            Administrador administrador;
            try {
                administrador = em.getReference(Administrador.class, nombre);
                administrador.getNombre();
            } catch (EntityNotFoundException enfe) {
                //En caso de que no exista el administrador, se lanza un error.
                throw new EntidadInexistenteException(
                        "No existe administrador con id " + nombre, enfe);
            }
            // Si existe el administrador, realizamos la acción correspondiete
            // y terminamos la transacción en la base de datos.
            em.remove(administrador);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                // Cerramos la conexión a la BD.
                em.close();
            }
        }
    }

    /**
     * Método encargado de obtener un listado de todos los administradores que
     * se encuentran almacenados en la base de datos.
     * @return - Lista de administradores registrados en la base de datos.
     */
    public List<Administrador> buscaAdministradores() {
        return buscaAdministradores(true, -1, -1);
    }

    /**
     * Método encargado de obtener un listado de todos los administradores que
     * se encuentran almacenados en la base de datos.
     * @param maxResultado - Índice hasta el que se obtienen las entidades.
     * @param primerResultado - Índice desde el que se obtienen las entidades.
     * @return Devuelve la lista con los administradores registrados en la BD.
     */
    public List<Administrador> buscaAdministradores(int maxResultado,
            int primerResultado) {
        return buscaAdministradores(false, maxResultado, primerResultado);
    }

    /**
     * Método encargado de obtener un listado de todos los administradores que
     * se encuentran almacenados en la base de datos.
     * @param todos - Indica si se obtienen todas las entidades.
     * @param maxResultado - Índice hasta el que se obtienen las entidades.
     * @param primerResultado - Índice desde el que se obtienen las entidades.
     * @return Devuelve la lista con los administradores registrados en la BD.
     */
    private List<Administrador> buscaAdministradores(boolean todos,
        int maxResultado, int primerResultado) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrador.class));
            Query q = em.createQuery(cq);
            if (!todos) {
                q.setMaxResults(maxResultado);
                q.setFirstResult(primerResultado);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Método que obtiene un administrador almacenado en la base de datos,
     * el cual es buscado a través de su nombre.
     * @param nombre - nombre del administrador a buscar.
     * @return Regresa el objeto administrador encontrado.
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
     * Método encargado de obtener el número de administradores
     * almacenados en la base de datos.
     * @return - el número de administradores registrados.
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