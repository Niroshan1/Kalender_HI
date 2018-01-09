/*
 * TODO: alles mit todo markiert + Metoden für getTermineImMonat & getTermineInWoche
 * implementieren
 * 
 */
package Utilities;

import java.util.LinkedList;
import java.io.Serializable;
import java.security.SecureRandom;

/**
 *
 * @author Tim Meyer
 */
public class Benutzer implements Serializable{
    
    private int userID;
    private final String username;
    private String vorname;
    private String nachname;
    private String email;
    private String passwort;
    private Terminkalender terminkalender;
    private LinkedList<String> kontaktliste; 
    private LinkedList<Meldungen> meldungen;
    private int meldungsCounter;
    
    /**
     * 
     * @param username
     * @param passwort
     * @param email
     * @throws BenutzerException 
     */
    Benutzer(String username, String passwort, String email, int userID) throws BenutzerException{
        if(username.length() < 4 || username.length() > 12){
            throw new BenutzerException("Der Username sollte zwischen 4 und 12 Zeichen lang sein");
        }
        if(passwort.length() < 4 || passwort.length() > 12){
            throw new BenutzerException("Das Passwort sollte zwischen 4 und 12 Zeichen lang sein");
        }
        
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.passwort = passwort;
        this.nachname = "";
        this.vorname = "";
        this.terminkalender = new Terminkalender(userID * 1000000 + 1);
        this.kontaktliste = new LinkedList<>();
        this.meldungen = new LinkedList<>();
        this.meldungsCounter = 1 + userID * 1000000;
    } 
    
    /**
     * 
     * @param username
     * @param passwort
     * @param email
     */
    Benutzer(String username, String passwort, String email, int userID, String vorname, String nachname, int meldungsCounter, int terminIDCounter){        
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
        this.terminkalender = new Terminkalender(terminIDCounter);
        this.kontaktliste = new LinkedList<>();
        this.meldungen = new LinkedList<>();
        this.meldungsCounter = meldungsCounter;
    }
    
    //Getter:
    public String getUsername(){
        return username;
    }
    public String getPasswort(){
        return passwort;
    }
    public String getNachname(){
        return nachname;
    }
    public String getVorname(){
        return vorname;
    }
    public String getEmail(){
        return email;
    } 
    public int getMeldungsCounter(){
        return meldungsCounter;
    }
    public Terminkalender getTerminkalender(){
        return terminkalender;
    }
    public final LinkedList<String> getKontaktliste(){
        return kontaktliste;
    }
    public LinkedList<Meldungen> getMeldungen(){
        return meldungen;
    }
    
    public int getUserID(){
        return this.userID;
    }
    public int getTerminCounter(){
        return terminkalender.getTerminCounter();
    }
    
    //Setter:
    public void setNachname(String nachname){
        this.nachname = nachname;
    }
    public void setVorname(String vorname){
        this.vorname = vorname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPasswort(String neuesPasswort) throws BenutzerException{
        if(neuesPasswort.length() < 4 || neuesPasswort.length() > 12){
            throw new BenutzerException("Das Passwort sollte zwischen 4 und 12 Zeichen lang sein");
        }
        this.passwort = neuesPasswort;
    }
    public void setKontaktliste(LinkedList<String> kontaktliste){
        this.kontaktliste = kontaktliste;
    }
    public void setMeldungen(LinkedList<Meldungen> meldungen){
        this.meldungen = meldungen;
    }
    
    
    
    public String resetPasswort(){
        String message;
        EMailService emailService = new EMailService();
        String allowedChars = "0123456789abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOP!?";
        SecureRandom random = new SecureRandom();
        StringBuilder pass = new StringBuilder(10);
    
        //zufälliges Passwort generieren (10 Zeichen)
        for (int i = 0; i < 10; i++) {
            pass.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
               
        passwort = pass.toString();
        message = "Ihr neues Passwort lautet: " + passwort ;
        emailService.sendMail(email, "Terminkalender: Passwort zurückgesetzt", message);
 
        return passwort;
    }
       
    /**
     * 
     * @param passwort
     * @return 
     */
    public boolean istPasswort(String passwort){
        return (this.passwort.equals(passwort));
    }
    
    /**
     * 
     * @param username 
     * @throws Terminkalender.BenutzerException 
     */
    public void addKontakt(String username) throws BenutzerException{
        if(username.equals(this.username)){
            throw new BenutzerException("Du kannst dich nicht selbst hinzufügen!");
        }
        for(String kontakt : kontaktliste){
            if(kontakt.equals(username)){
                throw new BenutzerException(username + " bereits in der Kontaktliste vorhanden!");
            }
        }
        kontaktliste.add(username);
    }
    
    /**
     * 
     * @param username 
     * @throws Terminkalender.BenutzerException 
     */
    public void removeKontakt(String username) throws BenutzerException{
        boolean inListe = false;
        for(String kontakt : kontaktliste){
            if(kontakt.equals(username)){
                inListe = true;
            }
        }
        if(!inListe){
            throw new BenutzerException(username + " nicht in der Kontaktliste vorhanden!");
        }
        kontaktliste.remove(username);
    }

    /**
     * 
     * @param datum
     * @param beginn
     * @param ende
     * @param titel
     * @return 
     * @throws TerminException 
     */
    public int addTermin(Datum datum, Zeit beginn, Zeit ende, String titel) throws TerminException{
        return terminkalender.addTermin(datum, beginn, ende, titel, username);
    }

    
    /**
     * 
     * @param termin
     */
    public void addTermin(Termin termin){
        terminkalender.addTermin(termin);
    }
    
    /**
     * 
     * @param termin
     * @param text
     * @param absender 
     * @return  
     */
    public int addAnfrage(Termin termin, String text, String absender){
        meldungen.add(new Anfrage(text ,termin, absender, meldungsCounter));
        meldungsCounter++;
        return meldungsCounter - 1;
    }
    
    /**
     *
     * @param meldung
     * @return 
     */
    public int addMeldung(String meldung){
        meldungen.add(new Meldungen(meldung, meldungsCounter));
        meldungsCounter++;
        return meldungsCounter - 1;
    }
    
    /**
     * 
     * @param index 
     */
    public void deleteMeldung(int index){
        meldungen.remove(index);
    }
    
    /**
     * 
     * @return 
     */
    public LinkedList<String> getProfil(){
        LinkedList<String> profil = new LinkedList<>();
        profil.add(this.username);
        profil.add(this.email);
        profil.add(this.vorname);
        profil.add(this.nachname);
        return profil;
    }
}
    

