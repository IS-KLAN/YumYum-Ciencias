package com.klan.proyecto.jpa.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para indicar que una entidad es inconsistente en la BD.
 * @author patlani
 */
public class InconsistenciasException extends Exception {
    /** Lista de mensajes */
    private List<String> messages;
    /**
     * Constructor de la excepción.
     * @param messages Lista de mensajes que se muestran.
     */
    public InconsistenciasException(List<String> messages) {
        super((messages != null && messages.size() > 0 ?
                messages.get(0) : null));
        if (messages == null) {
            this.messages = new ArrayList<String>();
        }
        else {
            this.messages = messages;
        }
    }
    /**
     * Método de acceso a la lista de mensajes.
     * @return Lista de mensajes.
     */
    public List<String> getMessages() {
        return messages;
    }
}
