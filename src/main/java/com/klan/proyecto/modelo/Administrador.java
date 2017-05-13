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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase para representar a un administrador.
 *
 * @author patlani
 */
@Entity
@Table(name = "administrador")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Administrador.busca",
            query = "SELECT a FROM Administrador a"),
    @NamedQuery(name = "Administrador.buscaNombre",
            query = "SELECT a FROM Administrador a WHERE a.nombre = :nombre")})
public class Administrador implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Nombre del administrador.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "nombre_administrador")
    private String nombre;
    /**
     * Contrasenia del administrador.
     */
    @Basic(optional = false)
    @Column(name = "contrase\u00f1a")
    private String contrasenia;

    /**
     * Constructor vacío con atributos no inicializados.
     */
    public Administrador() {
    }

    /**
     * Constructor con llave definida.
     *
     * @param nombre Nombre y llave del administrador.
     */
    public Administrador(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con atributos definidos.
     *
     * @param nombre Nombre del administrador.
     * @param contrasenia Contrasenia del administrador
     */
    public Administrador(String nombre, String contrasenia) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
    }

    /**
     * Método de acceso al nombre del administrador.
     *
     * @return String Devuelve el nombre del administrador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nombre de un administrador.
     *
     * @param nombre Nombre que se establece al administrador..
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso a la contraseña del administrador.
     *
     * @return String Devuelve la contraseña del administrador.
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Método que establece la contraseña del administrador.
     *
     * @param contrasenia Contrasenia del administrador.
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Método que genera el hashcode de un administrador.
     *
     * @return int Devuelve el hashcode generado por el nombre.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara 2 administradores para indicar si son
     * iguales.
     *
     * @param administrador Es el administrador con el que se
     * hace la comparación.
     * @return boolean Devuelve TRUE si los administradores on
     * iguales, FALSE en otro caso.
     */
    @Override
    public boolean equals(Object administrador) {
        if (!(administrador instanceof Administrador)) {
            return false;
        }
        Administrador otro = (Administrador) administrador;
        return !((this.nombre == null && otro.nombre != null)
                || (this.nombre != null && !this.nombre.equals(otro.nombre)));
    }

    /**
     * Método que representa a un administrador en una cadena.
     *
     * @return String Devuelve una cadena con el nombre del
     * administrador.
     */
    @Override
    public String toString() {
        return "Administrador[ nombre=" + nombre + " ]";
    }

}
