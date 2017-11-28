/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Tim Meyer
 */
public class BenutzerListe implements Serializable{
    
    private final LinkedList<Benutzer> benutzerliste;
    private int userIDCounter;
    
    public BenutzerListe(int userIDCounter){
        this.benutzerliste = new LinkedList<>();
        this.userIDCounter = userIDCounter;
    }
    
    /**
     * fügt der Benutzerliste einen Benutzer hinzu
     * 
     * @param username
     * @param passwort
     * @param email
     * @throws Terminkalender.BenutzerException
     */
    public void addBenutzer(String username, String passwort, String email) throws BenutzerException{
        if(existiertBenutzer(username)){
            throw new BenutzerException(username + " existiert bereits!");
        }
        benutzerliste.add(new Benutzer(username, passwort, email, userIDCounter));      
        userIDCounter++;
    }
    
    public void addBenutzer(Benutzer benutzer){
        if(!existiertBenutzer(benutzer.getUsername())){
            benutzerliste.add(benutzer); 
        }    
    }
    
    public int getIDCounter(){
        return this.userIDCounter;
    }
    
    public boolean existiertBenutzer(String username){
        boolean result = false;
        for(Benutzer benutzer : benutzerliste){
            if(benutzer.getUsername().equals(username)){
                result = true;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param username
     * @return 
     * @throws Terminkalender.BenutzerException 
     */
    public Benutzer getBenutzer(String username) throws BenutzerException{
        Benutzer result = null;        
        if(!existiertBenutzer(username)){
            throw new BenutzerException(username + " existiert nicht!");
        }
        for(Benutzer benutzer : benutzerliste){
            if(benutzer.getUsername().equals(username)){
                result = benutzer;
            }
        }
        return result;
    }
    
}

