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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Clase que representa a un usuario registrado en el sistema.
 *
 * @author patlani
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.busca",
            query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.buscaNombre",
            query = "SELECT u FROM Usuario u WHERE u.nombre = :nombre"),
    @NamedQuery(name = "Usuario.buscaCorreo",
            query = "SELECT u FROM Usuario u WHERE u.correo = :correo")})
public class Usuario implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Nombre del usuario.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "nombre_usuario")
    private String nombre;
    /**
     * Correo del usuario.
     */
    @Basic(optional = false)
    @Column(name = "correo")
    private String correo;
    /**
     * Contraseña del usuario.
     */
    @Basic(optional = false)
    @Column(name = "contrase\u00f1a")
    private String contrasenia;
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
     * Lista de evaluaciones.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<Evaluacion> evaluaciones;

    /**
     * Constructor vacío con atributos no inicializados.
     */
    public Usuario() {
    }

    /**
     * Constructor con llave inicializada.
     *
     * @param nombre Es el nombre y llave de acceso al usuario.
     */
    public Usuario(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con atributos obligatorios inicializados.
     *
     * @param nombre Nombre y llave del usuario.
     * @param correo Correo del usuario.
     * @param contrasenia Contraseña del usuario.
     */
    public Usuario(String nombre, String correo, String contrasenia) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    /**
     * Constructor con todos sus atributos inicializados
     *
     * @param nombre Nombre y llave del usuario.
     * @param correo Correo del usuario.
     * @param contrasenia Contraseña del usuario.
     * @param rutaImagen Ruta de la imagen del usuario.
     * @param datos Datos de la imagen del usuario.
     */
    public Usuario(String nombre, String correo, String contrasenia,
                                    String rutaImagen, byte[] datos) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rutaImagen = rutaImagen;
        this.datos = datos;
    }

    /**
     * Método de acceso al nombre del usuario.
     *
     * @return String Devuelve el nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nombre del usuario.
     *
     * @param nombre Nombre que se establece al usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso al correo del usuario.
     *
     * @return String Devuelve el correo del usuario.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Método que establece el correo del usuario.
     *
     * @param correo Correo que se establece al usuario.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Método de acceso a la constraseña del usuario.
     *
     * @return String Devuelve la contraseña del usuario.
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Método que establece la contraseña del usuario.
     *
     * @param contrasenia Contraseña que se establece al usuario.
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Método de acceso a la ruta de la imagen definida por el usuario.
     *
     * @return String Devuelve la ruta relativa de la imagen.
     */
    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Método que establece una ruta de la imagen definida por el usuario.
     *
     * @param rutaImagen Ruta que se establece a la imagen del usuario.
     */
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    /**
     * Método de acceso a los datos de la imagen definida por el usuario.
     *
     * @return byte[] Devuelve el conjunto de datos de la imagen definida.
     */
    public byte[] getDatos() {
        return datos;
    }

    /**
     * Método que establece los datos de la imagen definida por el usuario.
     *
     * @param datos Datos que se establecen a la imagen del usuario.
     */
    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    /**
     * Método de acceso a la lista de evaluaciones del usuario.
     *
     * @return Devuelve la lista de evaluaciones hechas por el usuario.
     */
    @XmlTransient
    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    /**
     * Método que establece una lista de evaluaciones para el usuario.
     *
     * @param evaluaciones Lista de evaluaciones que se establece al usuario.
     */
    public void setEvaluaciones(List<Evaluacion> evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    /**
     * Método que genera el hascode del objeto usuario para serializarlo.
     *
     * @return int Devuelve un entero que representa al objeto usuario por su
     * nombre.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara un usuario con otro para indicar si son iguales.
     *
     * @param usuario Usuario con el que se hace la comparación.
     * @return boolean Devuelve TRUE si los usuarios tienen atributos iguales,
     * FALSE en otro caso.
     */
    @Override
    public boolean equals(Object usuario) {
        if (!(usuario instanceof Usuario)) {
            return false;
        }
        Usuario otro = (Usuario) usuario;
        return !((this.nombre == null && otro.nombre != null)
                || (this.nombre != null && !this.nombre.equals(otro.nombre)));
    }

    /**
     * Método que representa en una cadena al Usuario.
     *
     * @return String Devuelve en una cadena con el nombre del usuario.
     */
    @Override
    public String toString() {
        return "Usuario[ nombre=" + nombre + " ]";
    }
}
