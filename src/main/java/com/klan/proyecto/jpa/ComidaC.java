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
 * Controlador JPA para insertar, editar o borrar Comida de la BD.
 * @author patlani
 */
public class ComidaC implements Serializable {

    /**
     * Constructor de la clase JPA de Comida.
     * @param emf - objeto EntityManagerFactory para la conexión a la BD.
     */
    public ComidaC(EntityManagerFactory emf) {
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
     * Método encargado de crear una nueva comida en la BD
     * @param comida - objeto comida que se creará
     * @throws EntidadExistenteException - se lanza esta excepción cuando ya
     * existe una comida con el mismo nombre en la BD
     * @throws Exception error ocurrido durante la creación de la comida.
     */
    public void crear(Comida comida) throws EntidadExistenteException,Exception{
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
        }catch (Exception ex) {
          if (buscaNombre(comida.getNombre()) != null) {
          throw new EntidadExistenteException(
                  "Comida " + comida + " ya existe.", ex);
          }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Método para editar una comida existente en la BD
     * @param comida- nombre de la comida que se editará
     * @throws EntidadInexistenteException - excepción que se lanza cuando la
     * comida que se desea editar no existe en la BD.
     * @throws Exception - error ocurrido durante la edición de la comida.
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
                throw new EntidadInexistenteException("No existe comida con id " 
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
     * Método para eliminar una comida de la BD
     * @param nombre- comida a eliminar
     * @throws EntidadInexistenteException - excepción que se lanza al no 
     * existir la comida que se desea borrar.
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
            }catch (EntityNotFoundException enfe) {
              throw new EntidadInexistenteException("No existe comida con id " 
                                                      + nombre, enfe);
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
     * Método encargado de obtener un listado de todas las comidas que
     * se encuentran almacenadas en la base de datos.
     * @return - Lista de comida registrada en la base de datos.
     */
    public List<Comida> buscaComidas() {
        return buscaComidas(true, -1, -1);
    }

    /**
     * Método encargado de obtener un listado de todas las comidas que
     * se encuentran almacenadas en la base de datos. 
     * @param resultadosMax - Indice hasta el que se obtienen las comidas
     * @param primerResultado - Indice a partir del cual se obtienen las comidas
     * @return - Lista con las comidas almacenadas en la BD
     */
    public List<Comida> buscaComidas(int resultadosMax, int primerResultado) {
        return buscaComidas(false, resultadosMax, primerResultado);
    }

    /**
     * Método encargado de obtener un listado de todas las comidas que
     * se encuentran almacenadas en la base de datos.
     * @param todo - Indica si se obtienen todas las entidades
     * @param resultadosMax - Indice hasta el que se obtienen las comidas
     * @param primerResultado - Indice a partir del cual se obtienen las comidas
     * @return - Lista con las comidas almacenadas en la BD
     */
    private List<Comida> buscaComidas(boolean todo, int resultadosMax, 
                                        int primerResultado) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comida.class));
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
     * Método que obtiene una comida almacenada en la base de datos,
     * la cual es buscada a través de su nombre.
     * @param nombre - nombre de la comida a buscar.
     * @return Regresa el objeto comida encontrado.
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
     * Método encargado de obtener el número de comidas
     * almacenados en la base de datos.
     * @return - el número de comidas registradas.
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
