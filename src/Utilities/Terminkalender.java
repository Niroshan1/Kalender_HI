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
public class Terminkalender implements Serializable{

    private final LinkedList<Termin> terminkalender;
    private int terminCounter;
    
    Terminkalender(int terminCounter){
        terminkalender = new LinkedList<>();
        this.terminCounter = terminCounter;
    }
       
    /**
     * 
     * @param id
     * @return 
     * @throws Terminkalender.TerminException 
     */
    public Termin getTerminByID(int id) throws TerminException{
        for(Termin termin : terminkalender){
            if(termin.getID() == id){
                return termin;
            }
        }
        throw new TerminException("kein Termin mit dieser ID vorhannden");
    }
    
    /**
     * 
     * @param datum
     * @param beginn
     * @param ende
     * @param titel
     * @param username
     * @return 
     * @throws TerminException 
     */
    public int addTermin(Datum datum, Zeit beginn, Zeit ende, String titel, String username) throws TerminException{
        terminkalender.add(new Termin(datum, beginn, ende, titel, terminCounter, username));
        terminCounter++;
        return terminCounter - 1;
    }
    
    /**
     * Hilfsmethode für getTerminImMonat und getTerminInWoche
     * 
     * @param termin 
     */
    public void addTermin(Termin termin){
        terminkalender.add(termin);
    }
    
    /**
     * gibt alle Termine im Monat 'monat' in LinkedList zurück
     * 
     * @param monat
     * @param jahr
     * @return 
     * @throws Terminkalender.TerminException 
     */
    public LinkedList<Termin> getTermineImMonat(int monat, int jahr) throws TerminException{
        LinkedList<Termin> monatsauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().getMonat() == monat && termin.getDatum().getJahr() == jahr){
                monatsauszug.add(termin);
            }
        }    
        return monatsauszug;
    }
    
    /**
     * 
     * @param datum
     * @return
     * @throws TerminException 
     */
    public LinkedList<Termin> getTermineAmTag(Datum datum) throws TerminException{
        LinkedList<Termin> monatsauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().equal(datum)){
                monatsauszug.add(termin);         
            }
        }    
        return monatsauszug;
    }
    
    /**
     * gibt alle Termine der übergebenen Kalenderwoche in LinkedList zurück
     * 
     * @param kalenderwoche
     * @param jahr
     * @return 
     */
    public LinkedList<Termin> getTermineInWoche(int kalenderwoche, int jahr) {
        LinkedList<Termin> wochenauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().getKalenderwoche() == kalenderwoche && termin.getDatum().getJahr() == jahr){
                wochenauszug.add(termin);
            }
        }       
        return wochenauszug;
    }

    /**
     * 
     * @param id 
     * @throws Terminkalender.TerminException 
     */
    public void removeTerminByID(int id) throws TerminException{
        terminkalender.remove(getTerminByID(id));
    }
    
    public int getTerminCounter(){
        return terminCounter;
    }
    
}