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
public class Termin implements Serializable{   
    
    private final int id;
    private Datum datum;
    private Zeit beginn;
    private Zeit ende;
    private String titel;
    private String notiz;
    private String ort;
    private LinkedList<Teilnehmer>teilnehmer;
    /* Rechte für Verwaltung: */
    private final String owner;
    private boolean editierbar;
      
    Termin(Datum datum, Zeit beginn, Zeit ende, String titel, int id, String username) throws TerminException{
        if(!anfangVorEnde(beginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        
        this.id = id;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
        this.titel = titel;
        this.notiz = "";
        this.ort = "";
        this.teilnehmer = new LinkedList<>();
        this.teilnehmer.add(new Teilnehmer(username));
        teilnehmer.getFirst().setIstTeilnemer();
        this.owner = username;
        this.editierbar = true;
    }
    
    Termin(Datum datum, Zeit beginn, Zeit ende, String titel, int id, String owner, String ort, String notiz, Boolean editierbar) throws TerminException{
        if(!anfangVorEnde(beginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        
        this.id = id;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
        this.titel = titel;
        this.notiz = notiz;
        this.ort = ort;
        this.teilnehmer = new LinkedList<>();
        this.teilnehmer.add(new Teilnehmer(owner));
        teilnehmer.getFirst().setIstTeilnemer();
        this.owner = owner;
        this.editierbar = editierbar;
    }

    /**
     * fügt der Teilnehmerliste 'teilnehmer' den Teilnehmer 'usename' hinzu
     * 
     * @param username 
     * @throws Terminkalender.TerminException 
     */
    public void addTeilnehmer(String username) throws TerminException{
        for(Teilnehmer user : this.teilnehmer){
            if(user.getUsername().equals(username)){
                throw new TerminException(username + " bereits auf der Teilnehmerliste");
            }
        }
        teilnehmer.add(new Teilnehmer(username));
    }
    
    //Getter 
    public boolean getEditierbar(){
        return editierbar;
    }
    public String getOwner(){
        return owner;
    }
    public int getID(){
        return id;
    }
    public Datum getDatum(){
        return datum;
    }
    public Zeit getBeginn(){
        return beginn;
    }
    public Zeit getEnde(){
        return ende;
    }
    public String getTitel(){
        return titel;
    }
    public String getNotiz(){
        return notiz;
    }
    public String getOrt(){
        return ort;
    }
    public final LinkedList<Teilnehmer> getTeilnehmerliste(){
        return teilnehmer;
    }
    
    // Setter
    public void setEditierbar(boolean editierbar, String username) throws TerminException{
        if(!username.equals(this.owner)){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        this.editierbar = editierbar;
    }
    public void setNotiz(String notiz, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        this.notiz = notiz;
    }   
    public void setOrt(String ort, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        this.ort = ort;
    }
    public void setTitel(String neuerTitel, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        titel = neuerTitel;
    }
    public void setBeginn(Zeit neuerBeginn, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        if(!anfangVorEnde(neuerBeginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        beginn = neuerBeginn;
    }
    public void setEnde(Zeit neuesEnde, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        if(!anfangVorEnde(beginn, neuesEnde)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        ende = neuesEnde;
    }
    public void setDatum(Datum neuesDatum, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        datum = neuesDatum;
    }

        
    /**
     * 
     * @param username 
     * @throws Terminkalender.TerminException 
     */
    public void changeTeilnehmerNimmtTeil(String username) throws TerminException{
        boolean error = true;
        for(Teilnehmer tl : teilnehmer){
            if(tl.getUsername().equals(username)){
                tl.setIstTeilnemer();
                error = false;
            }
        }
        if(error){
            throw new TerminException(username + " nicht in der Teilnehmerliste vorhanden");
        }
    }
    
    /**
     * 
     * @param username
     * @throws TerminException 
     */
    public void removeTeilnehmer(String username) throws TerminException{
        boolean error = true;
        for(Teilnehmer tl : teilnehmer){
            if(tl.getUsername().equals(username)){
                teilnehmer.remove(tl);
            }
        }
        if(error){
            throw new TerminException(username + " nicht in der Teilnehmerliste vorhanden");
        }
    }
    
    /**
     * 
     * @param beginn
     * @param ende
     * @return 
     */
    private boolean anfangVorEnde(Zeit beginn, Zeit ende){
        boolean result = true;
        if(ende.getStunde() < beginn.getStunde()){
            result = false;
        }
        else if(ende.getStunde() == beginn.getStunde()){
            if(ende.getMinute() < beginn.getMinute()){
                result = false;
            }
        }
        return result;
    }
}
