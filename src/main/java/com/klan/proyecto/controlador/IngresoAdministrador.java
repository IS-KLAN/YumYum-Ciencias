/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;


import com.klan.proyecto.jpa.AdministradorC;
import com.klan.proyecto.modelo.Administrador;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 *
 * Bean que se utiliza para el manejo de inicio de sesión en
 * la aplicación web del administrador del sistema.
 * @author anahiqj
 */
@ManagedBean
@RequestScoped
public class IngresoAdministrador implements Serializable {
    /**
     * Identificador del administrador.
     */
    private String administrador;
    /**
     * Contraseña del administrador.
     */
    private String contrasena;
    /**
     * Obtiene información de todas las peticiones del administador.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación.
     */
    private final FacesContext faceContext;
    /**
     * Mensaje para mostrar al administador.
     */
    private FacesMessage mensaje;

    /**
     * Constructor para inicializar los valores de faceContext y
     * httpServletRequest.
     */
    public IngresoAdministrador() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.
                getExternalContext().getRequest();
    }

    /**
     * Obtiene el identificador del administrador.
     * @return - el identificador del administrador.
     */
    public String getAdministrador() {
        return administrador;
    }

    /**
     * Establece el identificador del administrador.
     * @param administrador - Identificador del administrador a establecer.
     */
    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    /**
     * Obtiene la contraseña del administrador.
     * @return la contraseña del administrador.
     */
    public String getContrasena() {
        return contrasena;
    }
    /**
     * Asigna la contraseña del administrador.
     * @param contrasena - contrasena a asignar.
     */ 
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Método encargado de permitir el ingreso del administrador al sistema.
     * @return - el nombre de la vista que va a responder.
     */
    public String ingresar() {
        // Se realiza la conexión a la BD.
        EntityManagerFactory emf = Persistence.
                createEntityManagerFactory("YumYum-Ciencias");
        // Se busca al administración por el nombre.
        Administrador admin = new AdministradorC(emf).
                buscaNombre(administrador);
        //Si se encuentra al administrador y la contraseña que ingresó
        //son iguales a las almacenadas en la BD, le da acceso al sistema.
        if (admin != null && admin.getContrasenia().equals(contrasena)) {
            httpServletRequest.getSession().setAttribute("administrador",
                    administrador);
            mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "¡Acceso correcto!", null);
            faceContext.addMessage(null, mensaje);
            // Se asegura que el mensaje se muestre después de la redirección.
            faceContext.getExternalContext().getFlash().setKeepMessages(true);
            return "index?faces-redirect=true";
        } else {
            mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Nombre de administrador o contraseña incorrecto", null);
            faceContext.addMessage(null, mensaje);
            return "ingresoAdministrador";
        }
    }

    /**
     * Método que indica si se tiene una sesión activa.
     * @return Devuelve true si hay una sesión iniciada o falso en otro caso.
     */
    public boolean accedido() {
        return httpServletRequest.getSession().
            getAttribute("administrador") != null;
    }
}