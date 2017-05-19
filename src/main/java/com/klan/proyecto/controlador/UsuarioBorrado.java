/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.modelo.Usuario; // Para el manejo de usuarios.
import com.klan.proyecto.jpa.UsuarioC; // Para entrar al controlador de Usuario.
import com.klan.proyecto.modelo.Evaluacion; //Para el manejo de evaluaciones.
import com.klan.proyecto.jpa.EvaluacionC; //Para el controlador de evaluaciones.

import java.util.List; // Para guardar listas obtenidas de consultas.
import javax.faces.bean.ManagedBean; // Para inyectar código dentro de un JSF.
import javax.faces.bean.RequestScoped; // Conservar instancia activa en request.
import javax.faces.context.FacesContext; //Para conocer el contexto de ejecución
import javax.servlet.http.HttpServletRequest; // Para manejar datos guardados.
import java.io.Serializable; // Conserva la persistencia de objetos.
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManagerFactory; // Para conectarse a la BD.
import javax.persistence.Persistence; // Definir parámetros de conexión a la BD.

/**
 * Clase bean implementada para el borrado de usuarios.
 * @author anahiqj
 */
@ManagedBean
@RequestScoped
public class UsuarioBorrado implements Serializable {
    /**
     * Obtiene información de las peticiones de usuario.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación.
     */
    private final FacesContext faceContext;

    /**
     * Constructor que inicializa las instancias de FaceContext y
     * HttpServerRequest.
     */
    public UsuarioBorrado() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.
                getExternalContext().getRequest();
    }
    /**
     * Método encargado de obtener la lista de usuarios registrados
     * en la base de datos.
     * @return Lista de usuarios registrados.
     */
    public List<Usuario> usuarios() {
        // Se realiza la conexión a la BD.
        EntityManagerFactory emf = Persistence.
                createEntityManagerFactory("YumYum-Ciencias");
        UsuarioC usuario = new UsuarioC(emf);
        //Agregamos en una lista todos los usuarios que están registrados.
        List<Usuario> usuarios = usuario.buscaUsuarios();
        //Regresamos la lista de usuarios encontrados.
        return usuarios;
    }
    /**
     * Método encargado de borrar un usuario que ha sido seleccionado.
     * @param usuario - Usuario al que se va a borrar el sistema
     * @return cadena que contiene la página a donde se redireccionará una
     * vez terminada la acción correspondiente.
     */
    public String borrarUsuario(Usuario usuario) {
        String nombre = usuario.getNombre();
        FacesMessage message;
        try {
            // Se realiza la conexión a la BD.
            EntityManagerFactory emf = Persistence.
                    createEntityManagerFactory("YumYum-Ciencias");
            borrarEvaluacionesUsuario(usuario);
            UsuarioC usuarioC = new UsuarioC(emf);
            usuarioC.borrar(nombre);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Usuario borrado exitosamente", null);
            faceContext.addMessage(null, message);
            faceContext.getExternalContext().getFlash().setKeepMessages(true);
            System.out.println("Usuario borrado " + nombre);
            return "eliminarUsuario?faces-redirect=true";
        } catch (Exception e) {
                System.err.println("Error al borrar al usuario:\n"
                        + e.getMessage());
        }
        message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Ha ocurrido un error, intente  más tarde :C", null);
        return "eliminaUsuario";
    }
    /**
     * Método encargado de obtener el nombre de un usuario que
     * ha sido seleccionado.
     * @param usuario - Usuario al que se quiere obtener el nombre
     * @return cadena con el nombre del usuario.
     */
    private String obtenerUsuarioSeleccionado(Usuario usuario) {
        return usuario.getNombre();
    }
    /**
     * Método encargado de borrar todos los comentarios que un usuario
     * realizó en los puestos.
     * @param usuario - Usuario al que se va a borrar el sistema.
     * @throws Exception - En caso de ocurrir algún error.
     */
    private void borrarEvaluacionesUsuario(Usuario usuario) throws Exception {
        List<Evaluacion> evaluacionUsuario = usuario.getEvaluaciones();
        EntityManagerFactory emf = Persistence.
                createEntityManagerFactory("YumYum-Ciencias");
        EvaluacionC evalBorrada = new EvaluacionC(emf);
        try {
            for (Evaluacion evaluacion : evaluacionUsuario) {
                evalBorrada.borrar(evaluacion.getLlave());
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex);
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }
}