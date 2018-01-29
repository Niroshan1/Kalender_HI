/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

public class Teilnehmer implements Serializable{
    
    //Variablen Deklaration und Initialisierung
    private final String username;
    private boolean nimmtTeil;
    
    /**
     * Konstriuktor
     * @param username 
     */
    Teilnehmer(String username){
        this.username = username;
        this.nimmtTeil = false; 
    }
    
    /**
     * Methode, zum bekommen des Usernamens
     * @return 
     */
    public String getUsername(){
        return username;
    }
    
    /**
     * Methode, die prueft, dass User ein Teilnehmer eines Termins ist
     * @return 
     */
    public boolean checkIstTeilnehmer(){
        return nimmtTeil;
    }
    
    /**
     * Methode, die vermerkt, dass User ein Teilnehmer eines Termins ist
     */
    public void setIstTeilnemer(){
        nimmtTeil = true;
    }
}