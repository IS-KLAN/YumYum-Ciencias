/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

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
        Puesto p = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto");
        // Se inicializa con el puesto encontrado.
        if (p == null) {
            return;
        }
        puesto = p;
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
}
