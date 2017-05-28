/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.jpa.PuestoC;
import com.klan.proyecto.modelo.Puesto; // Para construir un puesto.
import com.klan.proyecto.modelo.Evaluacion; // Para construir evaluaciones.

import java.util.List; // Para guardar listas obtenidas de consultas.
import javax.annotation.PostConstruct; // Acciones posteriores a la creación.
import javax.faces.bean.ManagedBean; // Para inyectar código dentro de un JSF.
import javax.faces.bean.RequestScoped; // Para que exista en un request.
import javax.faces.context.FacesContext; // Para conocer el contexto.
import javax.servlet.http.HttpServletRequest; // Para manejar datos guardados.
import java.io.Serializable; // Para conservar la persistencia de objetos.
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Clase bean que implementa la evaluación de un puesto y la
 * consulta de su contenido.
 *
 * @author patlani
 */
@ManagedBean
@RequestScoped
public class Contenido implements Serializable {

    /**
     * Puesto del que se muestra el contenido.
     */
    private Puesto puesto;
    /**
     * Calificación calculada del puesto.
     */
    private int calificacionGlobal = 0;
    /**
     * Obtiene información de las peticiones de usuario.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación
     */
    private final FacesContext faceContext;
    
    private String descripcion;
    private String rutaImagen;
    

    /**
     * Constructor que inicializa las instancias de
     * FaceContext y HttpServerRequest, así como las
     * variables necesarias para las consultas.
     */
    public Contenido() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext
                .getExternalContext().getRequest();
    }

    /**
     * Método que carga los datos de la BD que se van a
     * mostrar en el perfil del puesto.
     */
    @PostConstruct
    public void cargar() {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
        Puesto p = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto");
        // Se inicializa con el puesto encontrado.
        if (p == null) {
            return;
        }
        puesto = p;
        
         // Se construye la evaluación actual con sus atributos.
        //Puesto actual = new PuestoC(emf).buscaNombre(puesto.getNombre());

        // Se inicializa la última calificación asignada del usuario.
        descripcion = (puesto != null) ? puesto.getDescripcion() : "";
        rutaImagen = (puesto != null) ? puesto.getRutaImagen() : "";
        
        // Se calcula la evaluación global del puesto.
        List<Evaluacion> evaluaciones = getPuesto().getEvaluaciones();
        if (evaluaciones != null && evaluaciones.size() > 0) {
            calificacionGlobal = 0;
            for (Evaluacion e : evaluaciones) {
                calificacionGlobal += e.getCalificacion();
            }
            calificacionGlobal /= evaluaciones.size();
        }
    }
    
    /**
     * Método de acceso al puesto elegido por el usuario.
     *
     * @return Devuelve el puesto que el usuario haya
     * elegido.
     */
    public Puesto getPuesto() {
        return puesto;
    }

    /**
     * Calificacion del puesto segun las evaluaciones de los
     * usuarios.
     *
     * @return Devuelve el número de estrellas en promedio.
     */
    public int getCalificacionGlobal() {
        return calificacionGlobal;
    }

    /**
     * Metodo que indica si hay comida disponible.
     *
     * @return Devuelve true si hay comida en el puesto,
     * falso en otro caso.
     */
    public boolean comidaDisponible() {
        return getPuesto().getComidas().size() > 0;
    }

    /**
     * Metodo que decide las evaluaciones que se muestran
     * según el contenido disponible para un puesto.
     *
     * @return Devuelve el nombre de la página
     * correspondiente al contenido del puesto.
     */
    public boolean evaluacionesDisponibles() {
        return getPuesto().getEvaluaciones().size() > 0;
    }

    /**
     * Método que indica si hay un puesto cargado en la
     * sesión.
     *
     * @return boolean Devuelve TRUE si hay un puesto, FALSE
     * en otro caso.
     */
    public boolean contenidoDisponible() {
        return getPuesto() != null;
    }
    
    /**
     * Método que se encarga de capturar la evaluación
     * ingresada en la interfaz.
     */
    public void editarPuesto() {
        // Se realiza una conexión a la BD.
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
        // Se inicializa un C para realizar una consulta de evaluación.
        PuestoC controlador = new PuestoC(emf);
  
        // Se construye la evaluación actual con sus atributos.
        Puesto actual = new Puesto(puesto.getNombre());
        try { // Se busca la existencia previa de una evaluación.
            Puesto encontrado = controlador.buscaNombre(puesto.getNombre());
      
            if (encontrado != null) {
                controlador.editar(actual);
            } else {
                controlador.crear(actual);
            } // Se actualiza el contenido del puesto.
            puesto = new PuestoC(emf).buscaNombre(puesto.getNombre());
            httpServletRequest.getSession().setAttribute("puesto", puesto);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Error al guardar el CAMBIOS:"
                            + actual.toString(), ex.getMessage()));
        } // Si no ocurre una excepción se avisa al usuario.
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "CAMBIOS REALIZADOS.", null));
        
       
    }
}
