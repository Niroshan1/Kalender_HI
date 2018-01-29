/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author Tim Meyer
 */
public class BenutzerException extends Exception {

    private final String message;
        
    /**
     * 
     * @param message 
     */
    public BenutzerException(String message) {
        this.message = message;
    }
        
    /**
     * gibt die Nachricht zurueck
     * 
     * @return message
     */
    @Override
    public String getMessage(){
        return message;
    }
    
}
