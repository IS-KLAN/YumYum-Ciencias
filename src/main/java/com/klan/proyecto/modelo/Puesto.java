/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Clase para representar a los lugares o puestos de comida que se registren en
 * el sistema.
 *
 * @author patlani
 */
@Entity
@Table(name = "puesto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Puesto.busca",
            query = "SELECT p FROM Puesto p"),
    @NamedQuery(name = "Puesto.buscaNombre",
            query = "SELECT p FROM Puesto p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Puesto.buscaLugar",
            query = "SELECT p FROM Puesto p WHERE p.latitud = :latitud "
                        + "AND p.longitud = :longitud")})
public class Puesto implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Nombre del puesto.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "nombre_puesto")
    private String nombre;
    /**
     * Desxrpción del puesto.
     */
    @Column(name = "descripcion")
    private String descripcion;
    /**
     * Latitud del puesto.
     */
    @Basic(optional = false)
    @Column(name = "latitud")
    private String latitud;
    /**
     * Longitud del puesto.
     */
    @Basic(optional = false)
    @Column(name = "longitud")
    private String longitud;
    /**
     * Ruta de la imagen.
     */
    @Column(name = "rutaImagen")
    private String rutaImagen;
    /**
     * Datos de la imagen.
     */
    @Lob
    @Column(name = "datos")
    private byte[] datos;
    /**
     * Lista de comidas.
     */
    @ManyToMany(mappedBy = "puestos")
    private List<Comida> comidas;
    /**
     * Lista de evaluaciones.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "puesto")
    private List<Evaluacion> evaluaciones;

    /**
     * Constructor vacío con atributos no inicializados.
     */
    public Puesto() {
    }

    /**
     * Constrcutor con llave primaria definida.
     *
     * @param nombre Es el nombre y llave del puesto.
     */
    public Puesto(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con atributos obligatorios definidos.
     *
     * @param nombre Nombre del puesto
     * @param latitud Latitud del puesto.
     * @param longitud Longitud del puesto.
     */
    public Puesto(String nombre, String latitud, String longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Constructor con todos sus atributos definidos.
     *
     * @param nombre Nombre del puesto.
     * @param descripcion Descripción del puesto.
     * @param latitud Latitud del puesto.
     * @param longitud Longitud del puesto.
     * @param rutaImagen Ruta del archivo de imagen del puesto.
     * @param datos Datos de la imagen definida para el puesto.
     */
    public Puesto(String nombre, String descripcion, String latitud, String longitud,
                                 String rutaImagen, byte[] datos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.rutaImagen = rutaImagen;
        this.datos = datos;
    }

    /**
     * Método de acceso al nombre del puesto.
     *
     * @return Devuelve el nombre del peusto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nombre del puesto.
     *
     * @param nombre Nombre que se establece al puesto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso a la descripción del puesto.
     *
     * @return String Devuelve la descripción del puesto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Método que establece una descripción al puesto.
     *
     * @param descripcion Descripción que se establece al puesto.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Método de acceso a la latitud del puesto.
     *
     * @return String Devuelve la latitud del puesto.
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * Método que establece la latitud del puesto.
     *
     * @param latitud Latitud del puesto.
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * Método de acceso a la longitud del puesto.
     *
     * @return String Devuelve la longitud del puesto.
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Método que establece la longitud del puesto.
     *
     * @param longitud longitud del puesto.
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * Método de acceso a la ruta de la imagen del puesto.
     *
     * @return String Devuelve la ruta del archivo de la imagen del puesto.
     */
    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Método que establece la ruta del archivo de la imagen del puesto.
     *
     * @param rutaImagen Ruta relativa de la imagen del puesto.
     */
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    /**
     * Método de acceso a los datos de la imagen del puesto.
     *
     * @return byte[] Devuelve el conjunto de datos de la imagen del puesto.
     */
    public byte[] getDatos() {
        return datos;
    }

    /**
     * Método que establece los datos de la imagen del puesto.
     *
     * @param datos Datos del archivfvo de imagen del puesto.
     */
    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    /**
     * Método de acceso a la lista de comidas que tiene el puesto.
     *
     * @return Devuelve una lista de las comidas del puesto.
     */
    @XmlTransient
    public List<Comida> getComidas() {
        return comidas;
    }

    /**
     * Método que establece la lista de comidas del puesto.
     *
     * @param comidas Lista de comidas se establece al puesto.
     */
    public void setComidas(List<Comida> comidas) {
        this.comidas = comidas;
    }

    /**
     * Método de acceso a la lista de evaluaciones del puesto.
     *
     * @return Devuelve la lista de evaluaciones que tiene el puesto.
     */
    @XmlTransient
    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    /**
     * Método que establece una lista de evaluaciones del puesto.
     *
     * @param evaluaciones Lista de evaluaciones que se establece al puesto.
     */
    public void setEvaluaciones(List<Evaluacion> evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    /**
     * Método que genera el hashcode para serializar un objeto puesto.
     *
     * @return int Devuelve el hascode generado por el nombre del puesto.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara a dos puestos para saber si son iguales.
     *
     * @param puesto Puesto con el que se reliza la comparación.
     * @return boolean Devuelve TRUE si los puestos tienen los mismos atributos,
     * FALSE en otro caso.
     */
    @Override
    public boolean equals(Object puesto) {
        if (!(puesto instanceof Puesto)) {
            return false;
        }
        Puesto otro = (Puesto) puesto;
        return !((this.nombre == null && otro.nombre != null)
                || (this.nombre != null && !this.nombre.equals(otro.nombre)));
    }

    /**
     * Método que representa en una cadea a un puesto.
     *
     * @return String Devuelve 8una cadena con el nombre del puesto.
     */
    @Override
    public String toString() {
        return "Puesto[ nombre=" + nombre + " ]";
    }

}
