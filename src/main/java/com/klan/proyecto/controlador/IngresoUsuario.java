/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.jpa.PendienteC;
import com.klan.proyecto.jpa.UsuarioC; // Para consultar usuarios de la BD.
import com.klan.proyecto.modelo.Pendiente;
import com.klan.proyecto.modelo.Usuario; // Para construir un usuario.

import javax.faces.application.FacesMessage; // Para mostrar avisos.
import javax.faces.bean.ManagedBean; // Para inyectar código dentro de un JSF.
import javax.faces.bean.RequestScoped; // Instancia activa durante un request.
import javax.faces.context.FacesContext; // Conocer el contexto de ejecución.
import javax.persistence.EntityManagerFactory; // Para conectarse a la BD.
import javax.persistence.Persistence; //Definir los parámetros de conexión a BD.
import javax.servlet.http.HttpServletRequest; // Para manejar datos guardados.
import java.io.Serializable; // Persistencia de objetos que se guarden.

/**
 *
 * Bean utilizado para el ingreso de un usuario al sistema.
 * @author anahiqj
 */
@ManagedBean
@RequestScoped
public class IngresoUsuario implements Serializable {
    /**
     * Correo del usuario.
     */
    private String correo;
    /**
     * Contraseña del usuario.
     */
    private String contrasena;
    /**
     * Obtiene información de todas las peticiones de correo.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación.
     */
    private final FacesContext faceContext;
    /**
     * Mensaje que se le muestra al usuario.
     */
    private FacesMessage mensaje;

    /**
     * Constructor para inicializar los valores de faceContext y
     * httpServletRequest.
     */
    public IngresoUsuario() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.
            getExternalContext().getRequest();
    }

    /**
     * Obtiene el nombre de correo.
     *
     * @return El nombre de correo.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el nombre de correo.
     * @param correo El nombre de correo a establecer.
     */
    public void setCorreo (String correo) {
        this.correo = correo;
    }

    /**
     * Regresa la contraseña ingresada.
     *
     * @return La contraseña ingresada.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña.
     *
     * @param contrasena -La contraseña que ingresa el usuario.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Método encargado de ingresar los datos ingresados.
     * @return El nombre de la vista que va a responder.
     */
    public String ingresar() {
        // Se realiza la conexión a la BD.
        EntityManagerFactory emf = Persistence.
                createEntityManagerFactory("YumYum-Ciencias");
        // Se busca al usuario por correo.
        Usuario usuario = new UsuarioC(emf).buscaCorreo(correo);
        // Se verifica que el usuario exista en la BD y
        //que haya ingresado la contraseña corrrecta.
        if (usuario != null && usuario.getContrasenia().equals(contrasena)) {
        // Se guarda al usuario.
            httpServletRequest.getSession().setAttribute("usuario", usuario);
            mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Acceso Correcto", null);
            faceContext.addMessage(null, mensaje);
            // Se asegura que el mensaje se muestre después de la redirección.
            faceContext.getExternalContext().getFlash().setKeepMessages(true);
            return "index?faces-redirect=true";
        }
        Pendiente posible = new PendienteC(emf).buscaCorreo(correo);
        if (posible != null) {
            mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debe confirmar su registro desde su correo para ingresar",
                    null);
            faceContext.addMessage(null, mensaje);
        } else { // Se informa el error si ha ocurrido algún error.
            mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Usuario o contraseña incorrecto", null);
            faceContext.addMessage(null, mensaje);
        }
        return "ingresoUsuario";
    }

    /**
     * Método que define las opciones disponibles de la barra de navegación.
     * @return Devuelve el xhtml que contiene las opciones correspondientes.
     */
    public String opcionesDisponibles() {
        if (httpServletRequest.getSession().getAttribute("usuario") != null) {
            return "opcionesUsuario";
        } else {
            return "opcionesInvitado";
        }
    }

    /**
     * Método que indica si se tiene una sesión activa.
     * @return Devuelve true si hay una sesión iniciada,
     * o falso en otro caso.
     */
    public boolean accedido() {
        return httpServletRequest.getSession().getAttribute("usuario") != null;
    }

    /**
     * Método que indica el nombre del usuario que se encuentre activo.
     * @return Devuelve el nombre del usuario activo o NULL en otro caso.
     */
    public String usuarioActivo() {
        Usuario u = ((Usuario) httpServletRequest.getSession().
            getAttribute("usuario"));
        return (u != null) ? u.getNombre() : null;
    }
}