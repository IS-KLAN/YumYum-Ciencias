/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klan.proyecto.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author patlani
 */
@Entity
@Table(name = "evaluacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Evaluacion.busca",
            query = "SELECT e FROM Evaluacion e"),
    @NamedQuery(name = "Evaluacion.buscaNombrePuesto",
            query = "SELECT e FROM Evaluacion e "
                        + "WHERE e.llave.nombrePuesto = :nombrePuesto"),
    @NamedQuery(name = "Evaluacion.buscaNombreUsuario",
            query = "SELECT e FROM Evaluacion e "
                        + "WHERE e.llave.nombreUsuario = :nombreUsuario")})
public class Evaluacion implements Serializable {

    /**
     * Variable para serializar.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Llave de la evaluación.
     */
    @EmbeddedId
    protected EvaluacionP llave;
    /**
     * Comentario.
     */
    @Column(name = "comentario")
    private String comentario;
    /**
     * Calificación.
     */
    @Basic(optional = false)
    @Column(name = "calificacion")
    private int calificacion;
    /**
     * Puesto relacionado.
     */
    @JoinColumn(name = "nombre_puesto",
            referencedColumnName = "nombre_puesto",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Puesto puesto;
    /**
     * Usuario relacionado.
     */
    @JoinColumn(name = "nombre_usuario",
            referencedColumnName = "nombre_usuario",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;

    /**
     * Constructor vacío con atributos no inicializados.
     */
    public Evaluacion() {
    }

    /**
     * Constructor con llave y atributos definidos.
     *
     * @param llave Es la llave que contiene el nombre del puesto y el usuario
     * de la evaluación.
     * @param comentario Es el comentario de la evaluación.
     * @param calificacion Es la calificación de la evaluación.
     */
    public Evaluacion(EvaluacionP llave, String comentario, int calificacion) {
        this.llave = llave;
        this.comentario = comentario;
        this.calificacion = calificacion;
    }

    /**
     * Constructor con llave definida.
     *
     * @param nombrePuesto Es el nombre del puesto en el que se hace la
     * evaluación.
     * @param nombreUsuario Es el nombre del usuario que hace la evaluación.
     */
    public Evaluacion(String nombrePuesto, String nombreUsuario) {
        this.llave = new EvaluacionP(nombrePuesto, nombreUsuario);
    }

    /**
     * Método de acceso a la llave de la evaluación.
     *
     * @return EValuacionP Devuelve la llave de la evaluación.
     */
    public EvaluacionP getLlave() {
        return llave;
    }

    /**
     * Método que establece la llave de la evaluación.
     *
     * @param llave Es la llave que se establece a la evaluación.
     */
    public void setLlave(EvaluacionP llave) {
        this.llave = llave;
    }

    /**
     * Método de acceso al comentario de una evaluación.
     *
     * @return String Devuelve el comntario que tiene la evaluación.
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Método que establece el comentario de la evaluación.
     *
     * @param comentario Es el comentario que se establece.
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * Método de acceso a la calificación de la evaluación.
     *
     * @return int Devuelve la calificación del puesto.
     */
    public int getCalificacion() {
        return calificacion;
    }

    /**
     * Método que establece la calificación del puesto.
     *
     * @param calificacion Calificación que se establece.
     */
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    /**
     * Método de acceso al puesto relacionado en la evaluación.
     *
     * @return Puesto Devuelve el puesto relacionado.
     */
    public Puesto getPuesto() {
        return puesto;
    }

    /**
     * Método que establece el puesto relacionado de la evaluación.
     *
     * @param puesto Devuelve el puesto relacionado de la evaluación.
     */
    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    /**
     * Método de acceso al usuario relacionado de la evaluación.
     *
     * @return Usuario Devuelve el usauario relacionado de la evaluación.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método que establece al usuario relacionado de la evaluación.
     *
     * @param usuario Usuario relacionado de la evaluación.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método que genera el hashcode para serializar un objeto Evaluación.
     *
     * @return int Devuelve el hascode generado por la llave de la evaluación.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (llave != null ? llave.hashCode() : 0);
        return hash;
    }

    /**
     * Método que compara 2 evaluaciones para indicar si son iguales.
     *
     * @param evaluacion Es la evaluación con la que se hace la comparación.
     * @return boolean Devuelve TRUE si las evaluaciones son iguales.
     */
    @Override
    public boolean equals(Object evaluacion) {
        if (!(evaluacion instanceof Evaluacion)) {
            return false;
        }
        Evaluacion otro = (Evaluacion) evaluacion;
        return !((this.llave == null && otro.llave != null) ||
                        (this.llave != null && !this.llave.equals(otro.llave)));
    }

    @Override
    public String toString() {
        return "Evaluacion[ llave=" + llave + " ]";
    }

}
