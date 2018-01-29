/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import Utilities.Benutzer;

public class Sitzung {
    
    // Variablen Deklaration und Initialisierung
    private final Benutzer eingeloggterBenutzer;
    private final int sitzungsID;
    
    /**
     * Konstruktur
     * @param benutzer
     * @param sitzungID 
     */
    public Sitzung(Benutzer benutzer, int sitzungID){
        this.eingeloggterBenutzer = benutzer;
        this.sitzungsID = sitzungID;
    }
    
    /**
     * Methode, die einzelne Sitzungen miteinander vergleicht
     * @param value
     * @return 
     */
    public boolean compareWithSitzungsID(int value){
        return sitzungsID == value;
    }
    
    /**
     * Methode, die die gerade eingeloggten User ausliest
     * @return 
     */
    public Benutzer getEingeloggterBenutzer(){
        return eingeloggterBenutzer;
    }
}
