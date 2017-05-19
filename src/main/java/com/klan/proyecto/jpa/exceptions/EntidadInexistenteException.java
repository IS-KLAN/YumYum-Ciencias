package com.klan.proyecto.jpa.exceptions;

/**
 * Clase para indicar que una entidad no existe en la BD.
 * @author patlani
 */
public class EntidadInexistenteException extends Exception {
    /**
     * Constructor de la excepción.
     * @param message Mensaje que se muestra.
     * @param cause Causa de la excepción.
     */
    public EntidadInexistenteException(String message,
            Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructor de la excepción.
     * @param message Mensaje que se muestra.
     */
    public EntidadInexistenteException(String message) {
        super(message);
    }
}
