/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;


import com.klan.proyecto.jpa.AdministradorC;
import com.klan.proyecto.modelo.Administrador;
import javax.faces.application.FacesMessage; // Para mostrar mensajes en la interfaz.
import javax.faces.bean.ManagedBean; // Para inyectar código en los JavaServletFaces.
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * Bean manejado qué se utiliza para el manejo de inicio de Sesión en
 * la aplicación web. *
 * @author anahiqj
 */
@ManagedBean // LEER LA DOCUMENTACIÖN DE ESTA ANOTACIÓN: Permite dar de alta al bean en la aplicación
@RequestScoped // Sólo está disponible a partir de peticiones al bean
public class IngresoAdministrador implements Serializable{

    private String administrador;
    private String contrasena;
    private final HttpServletRequest httpServletRequest; // Obtiene información de todas las peticiones de nombreUsuario.
    private final FacesContext faceContext; // Obtiene información de la aplicación
    private FacesMessage mensaje;

    /**
     * Constructor para inicializar los valores de faceContext y
     * httpServletRequest.
     */
    public IngresoAdministrador() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
    }

    /**
    /**
     * Obtiene el nombre de nombreUsuario.
     *
     * @return El nombre de nombreUsuario.
     */
    public String getAdministrador() {
        return administrador;
    }

    /**
     * Establece el nombre de nombreUsuario.
     *
     * @param administrador El nombre de nombreUsuario a establecer.
     */
    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }


    /**
     * Obtiene la contraseña de un usuario.
     *
     * @return la contraseña de un usuario.
     */    
    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Método encargado de capturar los datos ingresados.
     * @return El nombre de la vista que va a responder.
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
            if (httpServletRequest.getSession().getAttribute("usuario") != null)
                httpServletRequest.getSession().removeAttribute("usuario");
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
     * @return Devuelve true si hay una sesión seHaIngresado, o falso en otro caso.
     */
    public boolean accedido() {
        return httpServletRequest.getSession().getAttribute("administrador") != null;
    }
    
}