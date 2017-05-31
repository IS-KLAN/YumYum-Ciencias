/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.jpa.PendienteC;
import com.klan.proyecto.jpa.PuestoC;
import com.klan.proyecto.jpa.UsuarioC; // Para consultar usuarios de la BD.
import com.klan.proyecto.modelo.Comida;
import com.klan.proyecto.modelo.Pendiente;
import com.klan.proyecto.modelo.Puesto;
import com.klan.proyecto.modelo.Usuario; // Para construir un usuario.
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.faces.application.FacesMessage; // Para mostrar y obtener mensajes de avisos.
import javax.faces.bean.ManagedBean; // Para inyectar código dentro de un JSF.
import javax.faces.bean.RequestScoped; // Para que la instancia se conserve activa durante un request.
import javax.faces.context.FacesContext; // Para conocer el contexto de ejecución.
import javax.persistence.EntityManagerFactory; // Para conectarse a la BD.
import javax.persistence.Persistence; // Para definir los parámetros de conexión a la BD.
import javax.servlet.http.HttpServletRequest; // Para manejar datos guardados.
import java.io.Serializable; // Para conservar la persistencia de objetos que se guarden.
import java.util.List;
import org.primefaces.model.UploadedFile;

/**
 *
 * Bean utilizado para pruebas al perfil de un puesto.
 * @author anahiqj
 */
@ManagedBean // LEER LA DOCUMENTACIÖN DE ESTA ANOTACIÓN: Permite dar de alta al bean en la aplicación
@RequestScoped // Sólo está disponible a partir de peticiones al bean
public class ModificacionPuesto implements Serializable{
    /**
     * Obtiene información de las peticiones de usuario.
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * Obtiene información de la aplicación.
     */
    private final FacesContext faceContext;

    private Puesto puesto;
        
    public String rutaImagen;
    
     
    public UploadedFile archivo;

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }
     
    public String descripcion;

   
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    /**
     * Constructor para inicializar los valores de faceContext y
     * httpServletRequest.
     */
    public ModificacionPuesto() {
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
    }

    public void cambiarDescripcion (){
        try{
            EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
            Puesto p = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto");
            System.out.println(p.getNombre());
            PuestoC puestoC = new PuestoC(emf);
            puestoC.actualizaDescripcion(p, this.descripcion);
            httpServletRequest.getSession().setAttribute("puesto", p);
                    FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "¡Descripción actualizada!.", null));
           
        } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Error al guardar descripcion controlador", null));
                System.err.println("Error al guardar descripcion controlador" + ex.getMessage());
            
        }
    }
    
    public void borrarComida(Comida comida) {
        FacesMessage message;
        try {
            // Se realiza la conexión a la BD.
            
            EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
            Puesto p = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto");
            PuestoC puestoC = new PuestoC(emf);
            List<Comida> nueva = p.getComidas();
            nueva.remove(comida);
            p.setComidas(nueva);
            puestoC.editar(p);
            httpServletRequest.getSession().setAttribute("puesto", p);
                    FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "¡Comida borrada exitosamente!", null));
        } catch (Exception e) {
                System.err.println(" Error al borrar la comida controlador" + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Error al borrar la comida controlador", e.getMessage()));
        } 
    }

    
    public void cambiarImagen() {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("YumYum-Ciencias");
        Puesto p = (Puesto) httpServletRequest.getSession()
                .getAttribute("puesto");
        final String dir = System.getProperty("user.dir").replace("\\", "/"); // Directorio de ejecución actual.
        final String sub = "/src/main/webapp/resources"; // Directorio especificado para guardar imagenes. 
        System.out.println("Archivo: " + sub);
        if (archivo != null && archivo.getSize() > 0) { // Sólo si se intenta cargar una rutaImagen.
            rutaImagen = p.getNombre() + "-" + p.getLatitud() + ".jpg"; // Se define el nombrePuesto de la rutaImagen.
            System.out.println("\n"+ rutaImagen+ "\n");
            try { // EL proceso de escritura en archivos puede lanzar excepciones.
                File f = new File(dir + sub, rutaImagen); // Se define el Directorio y Nombre con extensión del file.
                FileOutputStream output = new FileOutputStream(f); // Flujo de escritura para guardar la rutaImagen.
                InputStream input = archivo.getInputstream(); // Flujo de lectura para cargar el archivo subido.
                int read = 0; // Bandera para saber si se sigue leyendo bytes del archivo subido.
                byte[] bytes = new byte[1024]; // Buffer para cargar bloques de 1024 bytes (1 MegaByte).
                while ((read = input.read(bytes)) != -1) output.write(bytes, 0, read); // Se escribe el archivo con lo que se lee.
                
               
                
                PuestoC puestoC = new PuestoC(emf);
                puestoC.actualizaImagen(p, rutaImagen, bytes);
            
            } catch (Exception ex) {
                System.err.println(" Error al cambiar imagen" + ex.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Error al guardar la imagen", ex.getMessage()));
            }

        } else {
            rutaImagen = "default.jpg";
            System.out.println("No se cargó imagen :(");
        }
        
        httpServletRequest.getSession().setAttribute("puesto", p);
        FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_INFO,
        "¡Nueva imagen guardada exitosamente!", null));
    }
    
}