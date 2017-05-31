/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.jpa.PuestoC; // Para consultar puestos de la BD.
import com.klan.proyecto.jpa.EvaluacionC; // Para usar evaluaciones en la BD.
import com.klan.proyecto.modelo.Puesto; // Para construir un puesto.
import com.klan.proyecto.modelo.Usuario; // Para construir un usuario.
import com.klan.proyecto.modelo.Evaluacion; // Para construir evaluaciones.
import com.klan.proyecto.modelo.EvaluacionP; // Para construir llaves.

import javax.annotation.PostConstruct; // Para tareas posteriores.
import javax.faces.application.FacesMessage; // Para mensajes de avisos.
import javax.faces.bean.ManagedBean; // Para inyectar código dentro de un JSF.
import javax.faces.bean.RequestScoped; // Para que exista en un request.
import javax.faces.context.FacesContext; // Para conocer el contexto.
import javax.persistence.EntityManagerFactory; // Para conectarse a la BD.
import javax.persistence.Persistence; // Para definir la conexión.
import javax.servlet.http.HttpServletRequest; // Para manejar datos guardados.
import java.io.Serializable; // Para conservar la persistencia de objetos.

/**
 * Clase bean que implementa la evaluación de un puesto y la
 * consulta de su contenido.
 *
 * @author patlani
 */
@ManagedBean
@RequestScoped
public class Evaluador implements Serializable {

    /**
     * Usario que va a comentar.
     */
    private Usuario usuario;
    /**
     * Puesto del que se muestra el contenido.
     */
    private Puesto puesto;
    /**
     * Texto del comentario a guardar.
     */
    private String comentario = "";
    /**
     * Calificación que dará el usuario.
     */
    private int calificacion = 0;
    /**
     * Obtiene información de las peticiones de usuario.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación.
     */
    private final FacesContext faceContext;
    /**
     * Mensaje después de liminar comentario.
     */
    private String aviso;

    /**
     * Constructor que inicializa las instancias de
     * FaceContext y HttpServerRequest, así como las
     * variables necesarias para las consultas.
     */
    public Evaluador() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext
                .getExternalContext().getRequest();
        // Se obtienen los datos guardados del usuario.
        if ((usuario = (Usuario) httpServletRequest.getSession()
                .getAttribute("usuario")) == null) {
            usuario = new Usuario(); // Se inicializa por defecto.
        }
    }

    @PostConstruct
    public void cargar() {
        // Se realiza una conexión a la BD.
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
        // Se obtienen los datos guardados del puesto.
        if ((puesto = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto")) != null) {
            // Se obtiene la versión más actual del puesto en la BD.
            puesto = new PuestoC(emf).buscaNombre(puesto.getNombre());
        } else {
            puesto = new Puesto(); // Se inicializa por defecto.
        }        // Se construye la llave primaria del usuario encontrado.
        EvaluacionP id = new EvaluacionP(
                puesto.getNombre(), usuario.getNombre());
        // Se busca la evalución existente del usuario en el puesto definido.
        Evaluacion actual = new EvaluacionC(emf).buscaEvaluacion(id);
        // Se inicializa la última calificación asignada del usuario.
        calificacion = (actual != null) ? actual.getCalificacion() : 0;
        comentario = (actual != null) ? actual.getComentario() : "";
    }

    /**
     * Método de acceso al usuario de la sesión actual.
     *
     * @return El usuario que ha iniciado sesión.
     */
    public Usuario getUsuario() {
        return usuario;
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
     * Método de acceso al comentario que se ha escrito en
     * la interfaz.
     *
     * @return Devuelve el mensaje comentado en la interfaz.
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Método que establece el comentario del comentario que
     * será insertado.
     *
     * @param comentario Texto ingresado para ser guardado.
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * Método de acceso a la calificacion que se asigna en
     * la interfaz.
     *
     * @return Devuelve la calificacion asignada en la
     * interfaz.
     */
    public int getCalificacion() {
        return calificacion;
    }

    /**
     * Método que establece la calificacion que será
     * insertada.
     *
     * @param calificacion Puntaje ingresado para ser
     * guardado.
     */
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    /**
     * Método de acceso al mensaje de aviso.
     *
     * @return String Mensaje que se muestra.
     */
    public String getAviso() {
        return aviso;
    }

    /**
     * Mensaje que se establece.
     *
     * @param aviso Mensaje
     */
    public void setAviso(String aviso) {
        this.aviso = aviso;
    }

    /**
     * Método que se encarga de capturar la evaluación
     * ingresada en la interfaz.
     */
    public void evaluar() {
        // Se realiza una conexión a la BD.
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
        // Se inicializa un C para realizar una consulta de evaluación.
        EvaluacionC controlador = new EvaluacionC(emf);
        // Se construye la llave primaria de la evaluación actual.
        EvaluacionP id = new EvaluacionP(
                puesto.getNombre(), usuario.getNombre());
        
        // Se construye la evaluación actual con sus atributos.
        Evaluacion actual = new Evaluacion(id, comentario, calificacion);
        try { // Se busca la existencia previa de una evaluación.
            Evaluacion encontrada = controlador.buscaEvaluacion(id);
            if (encontrada != null) {
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
                            "Error al guardar el comentario:"
                            + actual.toString(), ex.getMessage()));
        } // Si no ocurre una excepción se avisa al usuario.
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Comentario Guardado.", null));
    }

    /**
     * Método que restablece la evaluación obtenida al
     * momento.
     */
    public void cancelar() {
        calificacion = 0;
        comentario = "";
    }
}
