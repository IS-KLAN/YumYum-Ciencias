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
 * Controlador JPA para insertar, editar, borrar y consultar
 * usuarios de la BD.
 *
 * @author patlani
 */
public class UsuarioC implements Serializable {

    /**
     * Constructor de la clase JPA de Administrador.
     *
     * @param emf - objeto EntityManagerFactory para la
     * conexión a la BD.
     */
    public UsuarioC(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Objeto de EntityManagerFactory encargado de la
     * conexión a la BD.
     */
    private EntityManagerFactory emf = null;

    /**
     * Método encargado de comenzar la conexión con la BD.
     *
     * @return - Creación de un objeto para la conexión a la
     * BD.
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Método encargado de la creación de un usuario al
     * sistema.
     *
     * @param usuario - objeto usuario que se
     * creará.
     * @throws EntidadExistenteException - excepción lanzada
     * cuando existen dos usuarios con nombres
     * duplicados.
     * @throws Exception - error ocurrido durante la
     * creación de usuario.
     */
    public void crear(Usuario usuario) throws EntidadExistenteException,
        Exception {
        if (usuario.getEvaluaciones() == null) {
            usuario.setEvaluaciones(new ArrayList<Evaluacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Evaluacion> attachedEvaluaciones = new ArrayList<Evaluacion>();
            for (Evaluacion evaluacion : usuario.getEvaluaciones()) {
                evaluacion = em.getReference(evaluacion.getClass(),
                    evaluacion.getLlave());
                attachedEvaluaciones.add(evaluacion);
            }
            usuario.setEvaluaciones(attachedEvaluaciones);
            em.persist(usuario);
            for (Evaluacion evaluacionesEvaluacion:usuario.getEvaluaciones()) {
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
                throw new EntidadExistenteException("Usuario " + usuario
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
     * Método que se encarga de editar los datos de un
     * usuario.
     *
     * @param usuario - objeto usuario que se va
     * a editar.
     * @throws InconsistenciasException - en caso de no
     * conservar consistente la entidad.
     * error.
     * @throws EntidadInexistenteException - en caso de no
     * existir tal usuario a editar, se lanza un
     * error.
     * @throws Exception- error ocurrido durante la creación
     * de usuario.
     */
    public void editar(Usuario usuario) throws InconsistenciasException,
        EntidadInexistenteException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class,
                usuario.getNombre());
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
            List<Evaluacion> attachedEvaluacionesNew =
                new ArrayList<Evaluacion>();
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
                    throw new EntidadInexistenteException("No existe usuario "
                        + id);
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
     * Método que se encarga de eliminar un usuario.
     *
     * @param nombre - nombre del usuario a borrar.
     * @throws InconsistenciasException - en caso de no
     * conservar consistente la entidad.
     * @throws EntidadInexistenteException - en caso de no
     * existir tal usuario a eliminar, se lanza un
     * error.
     */
    public void borrar(String nombre)
                   throws InconsistenciasException,
                   EntidadInexistenteException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, nombre);
                usuario.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadInexistenteException("No existe usuario "
                    + nombre, enfe);
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
     * Método encargado de obtener un listado de todos los
     * usuarios que se encuentran almacenados en la
     * base de datos.
     *
     * @return - Lista de usuarios registrados en la
     * base de datos.
     */
    public List<Usuario> buscaUsuarios() {
        return buscaUsuarios(true, -1, -1);
    }

    /**
     * Método encargado de obtener un listado de todos los
     * usuarios que se encuentran almacenados en la
     * base de datos.
     *
     * @param maxResultado - Índice hasta el que se obtienen
     * las entidades.
     * @param primerResultado - Índice desde el que se
     * obtienen las entidades.
     * @return Devuelve la lista con los usuarios
     * registrados en la BD.
     */
    public List<Usuario> buscaUsuarios(int maxResultado, int primerResultado) {
        return buscaUsuarios(false, maxResultado, primerResultado);
    }

    /**
     * Método encargado de obtener un listado de todos los
     * usuarios que se encuentran almacenados en la
     * base de datos.
     *
     * @param todos - Indica si se obtienen todas las
     * entidades.
     * @param maxResultado - Índice hasta el que se obtienen
     * las entidades.
     * @param primerResultado - Índice desde el que se
     * obtienen las entidades.
     * @return Devuelve la lista con los usuarios
     * registrados en la BD.
     */
    private List<Usuario> buscaUsuarios(boolean todos, int maxResultado,
        int primerResultado) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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
     * Método que obtiene un usuario almacenado en la
     * base de datos, el cual es buscado a través de su
     * nombre.
     *
     * @param nombre - nombre del usuario a buscar.
     * @return Regresa el objeto usuario encontrado.
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
     * Método que obtiene una usuario almacenado en la
     * base de datos, el cual es buscado a través de su
     * nombre.
     *
     * @param correo - correo del usuario a buscar.
     * @return Regresa el objeto usuario encontrado.
     */
    public Usuario buscaCorreo(String correo) {
        try {
            EntityManager em = getEntityManager();
            return (Usuario) (em.createNamedQuery("Usuario.buscaCorreo")
                    .setParameter("correo", correo).getSingleResult());
        } catch (Exception ex) {
            System.err.println("Error al buscar el usuario con correo: "
                                                + correo + ex.getMessage());
        }
        return null;
    }
    /**
     * Método encargado de obtener el número de usuarios
     * almacenados en la base de datos.
     *
     * @return - el número de usuarios registrados.
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
