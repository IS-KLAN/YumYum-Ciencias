/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clase para representar la llave primaria de una evaluación.
 *
 * @author patlani
 */
@Embeddable
public class EvaluacionP implements Serializable {

    /**
     * Nombre del puesto relacionado.
     */
    @Basic(optional = false)
    @Column(name = "nombre_puesto")
    private String nombrePuesto;
    /**
     * Nombre del usuario relacionado.
     */
    @Basic(optional = false)
    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    /**
     * Constructor vacío con atrbibutos no inicializados.
     */
    public EvaluacionP() {
    }

    /**
     * Constructor con atributos inicializados.
     *
     * @param nombrePuesto Nombre y llave del puesto relacionado.
     * @param nombreUsuario Nombre y llave del usuario relacionado.
     */
    public EvaluacionP(String nombrePuesto, String nombreUsuario) {
        this.nombrePuesto = nombrePuesto;
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Método de acceso al nombre del puesto en el que se hizo la evaluación.
     *
     * @return String Nombre del puesto relacionado.
     */
    public String getNombrePuesto() {
        return nombrePuesto;
    }

    /**
     * Método que establece el nombre del puesto en el que se hizo la evauación.
     *
     * @param nombrePuesto Nombre del puesto relacionado.
     */
    public void setNombrePuesto(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }

    /**
     * Método de acceso al nombre del usuario que hizo la evalación.
     *
     * @return String Nombre del usuario relacionado.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Método que establece el nombre del usuario que hizo la evaluación.
     *
     * @param nombreUsuario Nombre del usuario relacionado.
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Método que genera el hashcode para serializar un objeto llave.
     *
     * @return int Devuelve el hascode generado por los atributos de la llave.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombrePuesto != null ? nombrePuesto.hashCode() : 0);
        hash += (nombreUsuario != null ? nombreUsuario.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara dos llaves para indicar si son iguales.
     *
     * @param llave Es la llave con la que se realiza la evaluación.
     * @return boolean Devuelve TRUE si las llaves son iguales, FALSE en otro
     * caso.
     */
    @Override
    public boolean equals(Object llave) {
        if (!(llave instanceof EvaluacionP)) {
            return false;
        }
        EvaluacionP otro = (EvaluacionP) llave;
        if ((this.nombrePuesto == null && otro.nombrePuesto != null)
                || (this.nombrePuesto != null
                && !this.nombrePuesto.equals(otro.nombrePuesto))) {
            return false;
        }
        return !((this.nombreUsuario == null && otro.nombreUsuario != null)
                || (this.nombreUsuario != null
                && !this.nombreUsuario.equals(otro.nombreUsuario)));
    }

    /**
     * Método que devuelve una cadena con la información de la llave.
     *
     * @return Devuelve una cadena con los nombres del puesto y usuario de la
     * llave.
     */
    @Override
    public String toString() {
        return "EvaluacionPK[" + nombrePuesto + ", " + nombreUsuario + " ]";
    }

}
