/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;


public class DatenbankException extends Exception {
    // Variablen Deklaration und Initialisierung
    private final String message;
    
    /**
     * Konstruktur
     * @param message 
     */
    public DatenbankException(String message) {
        this.message = message;
    }
    
    /**
     * Gibt Meldung der Datenbank zurück
     * @return 
     */
    @Override
    public String getMessage(){
        return this.message;
    }
}
