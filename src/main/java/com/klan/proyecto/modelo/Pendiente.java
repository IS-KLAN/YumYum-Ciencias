/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase para representar a usuarios que tienen su confirmación
 * de registro pendiente.
 *
 * @author patlani
 */
@Entity
@Table(name = "pendiente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pendiente.busca",
            query = "SELECT p FROM Pendiente p"),
    @NamedQuery(name = "Pendiente.buscaNombre",
            query = "SELECT p FROM Pendiente p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Pendiente.buscaCorreo",
            query = "SELECT p FROM Pendiente p WHERE p.correo = :correo")})
public class Pendiente implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Llave.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "nombre_usuario")
    private String nombre;
    /**
     * Correo.
     */
    @Basic(optional = false)
    @Column(name = "correo")
    private String correo;
    /**
     * Contraseña.
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
     * Constructor vacío con atributos no inicializados.
     */
    public Pendiente() {
    }

    /**
     * Constructor con llave definida.
     *
     * @param nombre Nombre y llave del usuario pendiente.
     */
    public Pendiente(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con atributos definidos.
     *
     * @param nombre Nombre del usuario pendiente.
     * @param correo Correo del usuario pendiente.
     * @param contrasenia Contraseña del usuario pendiente.
     */
    public Pendiente(String nombre, String correo, String contrasenia) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    /**
     * Constructor con todos sus atributos inicializados
     *
     * @param nombre Nombre y llave del usuario pendiente.
     * @param correo Correo del usuario pendiente.
     * @param contrasenia Contraseña del usuario pendiente.
     * @param rutaImagen Ruta de la imagen del usuario
     * pendiente.
     * @param datos Datos de la imagen del usuario pendiente.
     */
    public Pendiente(String nombre, String correo, String contrasenia,
            String rutaImagen, byte[] datos) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rutaImagen = rutaImagen;
        this.datos = datos;
    }

    /**
     * Método de acceso al nombre del usuario pendiente.
     *
     * @return String Devuelve el nombre del usuario pendiente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nombre del usuario pendiente.
     *
     * @param nombre Nombre que se establece al usuario
     * pendiente.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso al correo del usuario pendiente.
     *
     * @return String Devuelve el correo del usuario pendiente.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Método que establece el correo del usuario pendiente.
     *
     * @param correo Correo que se establece al usuario
     * pendiente.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Método de acceso a la constraseña del usuario pendiente.
     *
     * @return String Devuelve la contraseña del usuario
     * pendiente.
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Método que establece la contraseña del usuario pendiente.
     *
     * @param contrasenia Contraseña que se establece al usuario
     * pendiente.
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Método de acceso a la ruta de la imagen definida por el
     * usuario pendiente.
     *
     * @return String Devuelve la ruta relativa de la imagen.
     */
    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Método que establece una ruta de la imagen definida por
     * el usuario pendiente.
     *
     * @param rutaImagen Ruta que se establece a la imagen del
     * usuario pendiente.
     */
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    /**
     * Método de acceso a los datos de la imagen definida por el
     * usuario pendiente
     *
     * @return byte[] Devuelve el conjunto de datos de la imagen
     * definida.
     */
    public byte[] getDatos() {
        return datos;
    }

    /**
     * Método que establece los datos de la imagen definida por
     * el usuario pendiente
     *
     * @param datos Datos que se establecen a la imagen del
     * usuario pendiente
     */
    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    /**
     * Método que genera el hascode del objeto usuario para
     * serializarlo.
     *
     * @return int Devuelve un entero que representa al objeto
     * usuario por su nombre.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara un usuario con otro para indicar si
     * son iguales.
     *
     * @param pendiente Usuario pendiente con el que se hace la
     * comparación.
     * @return boolean Devuelve TRUE si los usuarios tienen
     * atributos iguales, FALSE en otro caso.
     */
    @Override
    public boolean equals(Object pendiente) {
        if (!(pendiente instanceof Pendiente)) {
            return false;
        }
        Pendiente otro = (Pendiente) pendiente;
        return !((this.nombre == null && otro.nombre != null)
                || (this.nombre != null && !this.nombre.equals(otro.nombre)));
    }

    /**
     * Método que representa en una cadena al Usuario.
     *
     * @return String Devuelve en una cadena con el nombre del
     * usuario pendiente.
     */
    @Override
    public String toString() {
        return "Pendiente[ nombre=" + nombre + " ]";
    }

}
