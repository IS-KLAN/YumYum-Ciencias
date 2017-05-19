/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author patlani
 */
@Entity
@Table(name = "comida")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comida.busca",
            query = "SELECT c FROM Comida c"),
    @NamedQuery(name = "Comida.buscaNombre",
            query = "SELECT c FROM Comida c WHERE c.nombre = :nombre")})
public class Comida implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Nombre de la comida.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "nombre_comida")
    private String nombre;
    /**
     * Lista de puestos relacionados.
     */
    @JoinTable(name = "comida_puesto", joinColumns = {
        @JoinColumn(name = "nombre_comida",
                referencedColumnName = "nombre_comida")},
            inverseJoinColumns = {
                @JoinColumn(name = "nombre_puesto",
                        referencedColumnName = "nombre_puesto")})
    @ManyToMany
    private List<Puesto> puestos;

    /**
     * Constructor vacío con atributos no inicializdos.
     */
    public Comida() {
    }

    /**
     * Constructor con nombre definido.
     *
     * @param nombre Es el nombre de la comida.
     */
    public Comida(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso al nombre de la comida.
     *
     * @return String Devuelve el nombre de la comida.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nomnbre de la comida.
     *
     * @param nombre Nombre de la comida.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método de acceso a los puestos relacionados con la comida.
     *
     * @return Devuelve la lista de puestos relacionados..
     */
    @XmlTransient
    public List<Puesto> getPuestos() {
        return puestos;
    }

    /**
     * Método que establece la lista de puestos relacionados a la comida.
     *
     * @param puestos Lista de puestos que se establece.
     */
    public void setPuestos(List<Puesto> puestos) {
        this.puestos = puestos;
    }

    /**
     * Método que genera el hashcode de un objeto Comida.
     *
     * @return int Devuelve el hashcode generado por el nombre de la comida.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara 2 comidas para indicar si son iguales.
     *
     * @param comida Es la comida con la que se hace la comparación.
     * @return boolean Devuelve TRUE si las comidas son iguales, FALSE en otro
     * caso.
     */
    @Override
    public boolean equals(Object comida) {
        if (!(comida instanceof Comida)) {
            return false;
        }
        Comida otro = (Comida) comida;
        return !((this.nombre == null && otro.nombre != null)
                || (this.nombre != null && !this.nombre.equals(otro.nombre)));
    }

    /**
     * Método que representa una comida con una cadena.
     *
     * @return String devuelve una cadena con el nombre de la comida.
     */
    @Override
    public String toString() {
        return "Comida[ nombre=" + nombre + " ]";
    }

}
