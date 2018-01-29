/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

public class Meldung implements Serializable{
    
    //Variablen Deklaration und Initialisierung
    public final String text;
    public boolean gelesen;
    public int meldungsID;
    
    /**
     * Konstruktor
     * @param text
     * @param meldungsID 
     */
    public Meldung(String text, int meldungsID){
        this.text = text;
        this.gelesen = false;
        this.meldungsID = meldungsID;
    }
    
    /**
     * Methode, zur Prüfung ob Nachricht gelesen
     */
    public void meldungGelesen(){
        this.gelesen = true;
    }
    
    /**
     * Methode zum bekommen eines Textes
     * @return 
     */
    public String getText(){
        return this.text;
    }
    
    /**
     * Methode, um den Status zu bekommen
     * @return 
     */
    public boolean getStatus(){
        return this.gelesen;
    }
}
