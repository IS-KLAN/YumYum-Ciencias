/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.controlador;

import com.klan.proyecto.jpa.EvaluacionC;
import com.klan.proyecto.jpa.PuestoC;
import com.klan.proyecto.jpa.exceptions.EntidadInexistenteException;
import com.klan.proyecto.jpa.exceptions.InconsistenciasException;
import com.klan.proyecto.modelo.Comida;
import com.klan.proyecto.modelo.Evaluacion;
import com.klan.proyecto.modelo.Puesto;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author karla
 */
@ManagedBean
@RequestScoped
public class EliminarPuesto implements Serializable {

    private final EntityManagerFactory emf;
    private FacesContext faceContext;
    private HttpServletRequest httpServletRequest;

    /**
     * 
     */
    
    public EliminarPuesto() {
        emf = Persistence.createEntityManagerFactory("YumYum-Ciencias");
    }
    
    /**
     * 
     * @return 
     */
    public String borrar() {
      Puesto actual;
      FacesMessage mensaje;
      faceContext = FacesContext.getCurrentInstance();
      httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
      actual = (Puesto) httpServletRequest.getSession().getAttribute("puesto");

        try {
            List<Comida> listaComida = new ArrayList<>();
            actual.setComidas(listaComida);
            
            borraEvaluaciones(actual);
            
            if(!borraImagen(actual)){
                System.out.println("Imagen no encontrada");
            } else {
                System.out.println("Imagen borrada");
            } 
            
            borraPuesto(actual);
        } catch (IOException | EntidadInexistenteException ex){
            
        }

        mensaje= new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Puesto borrado",null);
        faceContext.addMessage(null, mensaje);
        faceContext.getExternalContext().getFlash().setKeepMessages(true);
        return "index?faces-redirect=true";
    }
       
    /**
     * 
     * @param p
     * @return 
     * @throws IOException 
     */
    public boolean borraImagen(Puesto p) throws IOException {
        boolean borrado=false;
        final String dir = System.getProperty("user.dir").replace("\\", "/");
        final String sub = "/src/main/webapp/resources";
        String nombre = p.getNombre() + "-" + p.getLatitud() + ".jpg";
        String ubicacion = dir + sub;
        String archivos;
        File folder = new File(ubicacion);
        File[] lArchivos = folder.listFiles();
        for (File arch: lArchivos) {
            if (arch.isFile()) {
                archivos = arch.getName();
                if (archivos.equals(nombre)) {
                    arch.delete();
                    borrado=true;
                    System.out.println("Imagen borrada");  
                }
            }
        } 
        return borrado;
    }
    
    /**
     * 
     * @param actual
     * @throws IOException 
     */
    public void borraPuesto(Puesto actual) throws IOException {
        String nombre = actual.getNombre();
        PuestoC pc = new PuestoC(emf);
        try {
            pc.borrar(nombre);
        } catch (InconsistenciasException | EntidadInexistenteException ex) {
        }   
        
    }
    
    public void borraEvaluaciones(Puesto p) throws IOException, EntidadInexistenteException {
        List<Evaluacion> evaluaciones = p.getEvaluaciones();
        EvaluacionC eval = new EvaluacionC(emf);
        for(Evaluacion evalua : evaluaciones){
            eval.borrar(evalua.getLlave());
        } 
    }
}
