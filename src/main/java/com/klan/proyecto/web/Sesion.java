/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.web;

import com.klan.proyecto.controlador.UsuarioC;
import com.klan.proyecto.modelo.Usuario;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 *
 * Bean manejado qué se utiliza para el manejo de inicio de Sesión en
 * la aplicación web.
 *
 * @author anahiqj
 */
@ManagedBean // LEER LA DOCUMENTACIÖN DE ESTA ANOTACIÓN: Permite dar de alta al bean en la aplicación
@RequestScoped // Sólo está disponible a partir de peticiones al bean
public class Sesion implements Serializable{

    private String nombreUsuario;
    private String contrasena;
    private final HttpServletRequest httpServletRequest; // Obtiene información de todas las peticiones de nombreUsuario.
    private final FacesContext faceContext; // Obtiene información de la aplicación
    private FacesMessage message;

    /**
     * Constructor para inicializar los valores de faceContext y
     * httpServletRequest.
     */
    public Sesion() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
    }

    /**
     * Obtiene el nombre de nombreUsuario.
     *
     * @return El nombre de nombreUsuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre de nombreUsuario.
     *
     * @param nombreUsuario El nombre de nombreUsuario a establecer.
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YumYum-Ciencias");
        List<Usuario> lista = new UsuarioC(emf).encontrarPorCorreo(nombreUsuario);
             
        if(lista.isEmpty()){
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña incorrecto", null);
            faceContext.addMessage(null, message);
            return "ingresoUsuario";
     
        }else{
            
            Usuario u = lista.remove(0);
            httpServletRequest.getSession().setAttribute("usuario", u);
            httpServletRequest.getSession().setAttribute("contrasena", u);
            if(contrasena.equals(u.getContraseña())){
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Acceso Correcto", null);
                faceContext.addMessage(null, message);
                return "index";
            }else{
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña incorrecto", null);
                faceContext.addMessage(null, message);
                return "ingresoUsuario";
            }
        }
    }

  
    public String opcionesDisponibles() {
        if (httpServletRequest.getSession().getAttribute("usuario") != null) return "opcionesUsuario";
        return "opcionesInvitado";
    }
}
