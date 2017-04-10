/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.web;

import java.util.List;

import com.klan.proyecto.controlador.ComidaPuestoC;
import com.klan.proyecto.controlador.EvaluacionC;
import com.klan.proyecto.controlador.exceptions.NonexistentEntityException;
import com.klan.proyecto.modelo.ComidaPuesto;
import com.klan.proyecto.modelo.Evaluacion;
import com.klan.proyecto.modelo.EvaluacionPK;
import com.klan.proyecto.modelo.Puesto;
import com.klan.proyecto.modelo.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.Serializable;

/**
 *
 * @author patlani
 */
@ManagedBean
@ViewScoped
public class Contenido implements Serializable{

    private Usuario usuario;
    private Puesto puesto;
    private String comentario = "";
    private int calificacion = 0;
    private double calificacionGlobal = 0.0;
    private List<Evaluacion> evaluaciones;
    private List<ComidaPuesto> comida;
    private final HttpServletRequest httpServletRequest; // Obtiene información de todas las peticiones de usuario.
    private final FacesContext faceContext; // Obtiene información de la aplicación

    /**
     * Constructor que inicializa las instancias de FaceContext y HttpServerRequest, así como
     * las variables necesarias para las consultas.
     */
    public Contenido() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        if (httpServletRequest.getSession().getAttribute("usuario") != null) {
            usuario = ((Usuario)httpServletRequest.getSession().getAttribute("usuario"));
        } else usuario = new Usuario("luis");
        if (httpServletRequest.getSession().getAttribute("puesto") != null) {
            puesto = ((Puesto)httpServletRequest.getSession().getAttribute("puesto"));
        } else puesto = new Puesto("harry");
    }
    
    @PostConstruct
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YumYum-Ciencias");
        String p = puesto.getNombrePuesto(), u = usuario.getNombreUsuario();
        Evaluacion actual = new EvaluacionC(emf).findEvaluacion(new EvaluacionPK(p, u));
        comida = new ComidaPuestoC(emf).findByNombrePuesto(p);
        evaluaciones = new EvaluacionC(emf).findByNombrePuesto(p);
        calificacion = (actual != null)? actual.getCalificacion() : 0;
        comentario = (actual != null)? actual.getComentario() : "";
        for (Evaluacion e : evaluaciones) calificacionGlobal += e.getCalificacion();
        calificacionGlobal /= evaluaciones.size();        
    }
    
    public void actualiza() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YumYum-Ciencias");
        String p = puesto.getNombrePuesto(), u = usuario.getNombreUsuario();
        Evaluacion actual = new EvaluacionC(emf).findEvaluacion(new EvaluacionPK(p, u));
        comida = new ComidaPuestoC(emf).findByNombrePuesto(p);
        evaluaciones = new EvaluacionC(emf).findByNombrePuesto(p);
        calificacion = (actual != null)? actual.getCalificacion() : 0;
        comentario = (actual != null)? actual.getComentario() : "";
        for (Evaluacion e : evaluaciones) calificacionGlobal += e.getCalificacion();
        calificacionGlobal /= evaluaciones.size();        
    }

    /**
     * Método de acceso al usuario de la sesión actual.
     * @return El usuario que ha iniciado sesión.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establecer el usuario de la sesión actual.
     * @param usuario Es el usuario correspondiente a la sesión actual.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método de acceso al puesto elegido por el usuario.
     * @return Devuelve el puesto que el usuario haya elegido.
     */
    public Puesto getPuesto() {
        return puesto;
    }

    /**
     * Establecer el puesto elegido por el usuario.
     * @param puesto El puesto que se haya elegido.
     */    
    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    /**
     * Método de acceso al comentario que se ha escrito en la interfaz.
     * @return Devuelve el mensaje comentado en la interfaz.
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Método que establece el comentario del comentario que será insertado.
     * @param comentario Texto ingresado para ser guardado.
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }    

    /**
     * Método de acceso a la calificacion que se asigna en la interfaz.
     * @return Devuelve la calificacion asignada en la interfaz.
     */
    public int getCalificacion() {
        return calificacion;
    }

    /**
     * Método que establece la calificacion que será insertada.
     * @param calificacion Puntaje ingresado para ser guardado.
     */
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }    

    /**
     * Calificacion del puesto segun las evaluaciones de los usuarios.
     * @return Devuelve el número de estrellas en promedio.
     */
    public double getCalificacionGlobal() {
        return calificacionGlobal;
    }

    /**
     * Se establece la calificacion global del puesto.
     * @param calificacionGlobal Calificacion promedio del puesto.
     */
    public void setCalificacionGlobal(double calificacionGlobal) {
        this.calificacionGlobal = calificacionGlobal;
    }
    
    
    /**
     * Método que se encarga de mostrar los comentarios disponibles a la interfaz.
     * @return Devuelve una lista con los comentarios obtenidos del puesto.
     */
    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    /**
     * Método que se encarga de mostrar los productos disponibles a la interfaz.
     * @return Devuelve una lista con los producto obtenidos del puesto.
     */
    public List<ComidaPuesto> getComida() {
        return comida;
    }

    /**
     * Método que se encarga de capturar el comentario ingresado en la interfaz.
     */
    public void comentar() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YumYum-Ciencias");
        EvaluacionC controlador = new EvaluacionC(emf);
        EvaluacionPK id = new EvaluacionPK(puesto.getNombrePuesto(), usuario.getNombreUsuario());
        Evaluacion actual = new Evaluacion(id, comentario, calificacion);
        System.out.println("usuario: " + usuario.getNombreUsuario());
        System.out.println("puesto: " + puesto.getNombrePuesto());
        System.out.println("comentario: " + comentario);
        System.out.println("calificación: " + calificacion);
        try{
            controlador.edit(actual);
        }catch(NonexistentEntityException nex){
            System.out.println("No se pudo editar la evaluación.");
            try{ controlador.create(actual);
            }catch(Exception ex){System.out.println("No se pudo insertar la evaluación.");}
        }catch(Exception ex){
            System.out.println("Error desconocido: " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
            (FacesMessage.SEVERITY_INFO, "Error al guardar el comentario:" + actual.toString(), null));
        }finally{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
            (FacesMessage.SEVERITY_INFO, "Comentario Guardado.", null));
            System.out.println("calificaciónGlobal: " + calificacionGlobal);
            actualiza();
            System.out.println("calificaciónGlobal: " + calificacionGlobal);
        }
    }
    
    /**
     * Método que restablece la evaluación obtenida al momento.
     */
    public void cancelar() {
        calificacion = 0;
        comentario = "";
    }
}