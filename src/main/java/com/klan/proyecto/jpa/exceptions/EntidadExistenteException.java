package com.klan.proyecto.jpa.exceptions;

/**
 * Clase para indicar que una entidad ya existe en la BD.
 * @author patlani
 */
public class EntidadExistenteException extends Exception {
    /**
     * Constructor de la excepción.
     * @param message Mensaje que se muestra.
     * @param cause Causa de la excepción.
     */
    public EntidadExistenteException(String message,
            Throwable cause) {
        super(message, cause);
    }
    
    
    /**
     * Constructor de la excepción.
     * @param message Mensaje que se muestra.
     */
    public EntidadExistenteException(String message) {
        super(message);
    }
}
