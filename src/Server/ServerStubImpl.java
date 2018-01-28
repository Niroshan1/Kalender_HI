package Server;

import Server.Utilities.DatenbankException;
import Server.Utilities.ServerIdUndAnzahlUser;
import Server.Utilities.Sitzung;
import Server.Utilities.UserAnServer;
import Server.Utilities.Verbindung;
import ServerThreads.VerbindungstestsChildsThread;
import Utilities.Anfrage;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Niroshan, Vincent
 */
public class ServerStubImpl implements ServerStub {

    private final ServerDaten serverDaten;

    ServerStubImpl(ServerDaten serverDaten) {
        this.serverDaten = serverDaten;
    }

    /**
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich
     * verbinden soll dient der Erzeugung einer beidseitigen Verbindung /
     * ungerichteten Verbindung
     *
     * @param childIP
     * @return childID Die neue ID wird zurückgegeben
     * @throws RemoteException
     * @throws AccessException
     */
    @Override
    public String initConnection(String childIP) throws RemoteException {
        try {
            String childID = this.serverDaten.primitiveDaten.getNewChildId();
            
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(childIP, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, childIP, childID);

            this.serverDaten.childConnection.add(verbindung);
            
            // Starte Thread, der die Verbindung zu anderen Servern testet
            new VerbindungstestsChildsThread(this.serverDaten, verbindung).start();

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu KindServer: ID  " + childID + " hergestellt!");
              
            return childID;
        } catch (NotBoundException | IOException e) {
            System.out.println("LOG * ---> Verbindung zu KindServer Fehler!");
            return null;
        }
        
       
    }

    /**
     * Methode um zu testen, ob noch eine Verbindung zum Server besteht
     *
     * @param senderIP
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean ping(String senderIP) throws RemoteException {
        boolean result = false;

        for (Verbindung childConnection : this.serverDaten.childConnection) {
            if (childConnection != null && childConnection.getIP().equals(senderIP)) {
                result = true;
            }
        }
        if (this.serverDaten.parent != null
                && this.serverDaten.parent.getIP().equals(senderIP)) {
            result = true;
        }

        return result;
    }
    
    /**
     * Gibt die ID des Servers zurueck
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
     public String getServerID() throws RemoteException{
        return this.serverDaten.primitiveDaten.serverID;         
    }
     
     /**
      * gibt die anzahl eingeloggter Benutzer des Server zurück
      * 
      * @return
      * @throws RemoteException 
      */
    @Override
    public int getAnzahlUser() throws RemoteException{
        return this.serverDaten.aktiveSitzungen.size();
    }      
    
    @Override
    public ServerIdUndAnzahlUser findServerForUser() throws RemoteException{
        int tmp;
        int min = this.serverDaten.aktiveSitzungen.size();
        String minServerIP = this.serverDaten.primitiveDaten.ownIP ;
        String serverID = this.serverDaten.primitiveDaten.serverID;
        
        //suche server mit wenigstern usern und gib ip dessen zurück
        for(Verbindung child : this.serverDaten.childConnection){
            tmp = child.getServerStub().getAnzahlUser();
            if(tmp < min){
                min = tmp;
                minServerIP = child.getIP();
                serverID = child.getID();
            }
        }
        
        return new ServerIdUndAnzahlUser(min, serverID, minServerIP);
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt die id des users zurück
     * 
     * @param username Username der gesucht wird
     * @return 
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public int findIdForUser(String username) throws RemoteException, SQLException{
        if(this.serverDaten.primitiveDaten.serverID.equals("0")){
            return this.serverDaten.datenbank.getUserID(username);
        }
        else{
            return this.serverDaten.parent.getServerStub().findIdForUser(username);
        }
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt dessen Profil zurück
     * 
     * @param userID
     * @return userattribute als liste zurück
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public LinkedList<String> findUserProfil(int userID) throws RemoteException, SQLException{
        if(this.serverDaten.primitiveDaten.serverID.equals("0")){
            return this.serverDaten.datenbank.getProfil(userID);
        }
        else{
            return this.serverDaten.parent.getServerStub().findUserProfil(userID);
        }
    }
    
    /**
     * falls root: gibt userdaten zurück
     * sonst: frage parent nach userdaten
     * 
     * @param username
     * @return
     * @throws RemoteException
     * @throws SQLException
     * @throws DatenbankException 
     */
    @Override
    public Benutzer getUser(String username) throws RemoteException, SQLException, DatenbankException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            return this.serverDaten.datenbank.getBenutzer(username);
        }
        else{
            return this.serverDaten.parent.getServerStub().getUser(username);
        }
    }
      
    /**
     * falls root: entferne user aus UserAnServerListe
     * sonst: gebe an parent weiter
     * 
     * @param username
     * @throws RemoteException 
     * @throws Utilities.BenutzerException 
     */
    @Override
    public void removeUserFromRootList(String username) throws RemoteException, BenutzerException{
        if(this.serverDaten.primitiveDaten.serverID.equals("0")){
            int index = -1, counter = 0;
            for(UserAnServer uas : this.serverDaten.userAnServerListe){
                if(uas.username.equals(username)){
                    //wenn ja, gibt ip dieses servers zurück
                    index = counter;
                }
                counter++;
            }
            if(index == -1){
                throw new BenutzerException("ClientStubImpl Line 179 index == -1 // username nicht in UserAnServerListe");
            }
            else{
                this.serverDaten.userAnServerListe.remove(index);
            }
        }
        else{
            this.serverDaten.parent.getServerStub().removeUserFromRootList(username);
        }
    }
    
    @Override
    public void changePasswort(String passwort, String username) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.changePasswort(username, passwort);
        }
        else{
            this.serverDaten.parent.getServerStub().changePasswort(passwort, username);
        }           
    }
    
    @Override
    public void changeVorname(String vorname, String username) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.changeVorname(vorname, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeVorname(username, vorname);
        }           
    }
    
    @Override
    public void changeNachname(String nachname, String username) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.changeNachname(nachname, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeNachname(nachname, username);
        }           
    }
    
    @Override
    public void changeEmail(String email, String username) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.changeEmail(email, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeEmail(email, username);
        }           
    }
    
    @Override
    public void addKontakt(String kontaktname, int userID) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.addKontakt(userID, kontaktname);
        }
        else{
            this.serverDaten.parent.getServerStub().addKontakt(kontaktname, userID);
        }    
    }
    
    @Override
    public void removeKontakt(String kontaktname, int userID) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.removeKontakt(userID, kontaktname);
        }
        else{
            this.serverDaten.parent.getServerStub().removeKontakt(kontaktname, userID);
        }    
    }
    
    @Override
    public void deleteMeldung(int meldungsID) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.deleteMeldung(meldungsID);
        }
        else{
            this.serverDaten.parent.getServerStub().deleteMeldung(meldungsID);
        }    
    }
    
    @Override
    public void setMeldungenGelesen(int meldungsID) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            this.serverDaten.datenbank.setMeldungenGelesen(meldungsID);
        }
        else{
            this.serverDaten.parent.getServerStub().setMeldungenGelesen(meldungsID);
        }    
    }
    
    @Override
    public int addNewTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int userID) throws RemoteException, SQLException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            int terminID = this.serverDaten.datenbank.getTerminIdCounter();
            this.serverDaten.datenbank.addNewTermin(datum, beginn, ende, titel, userID, terminID);
            return terminID;           
        }
        else{
            return this.serverDaten.parent.getServerStub().addNewTermin(datum, beginn, ende, titel, userID);
        }
    }
    
    @Override
    public void changeEditierrechteDB(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            //trage aktuallisierte Daten ein
            serverDaten.datenbank.changeEditierrechte(termin.getEditierbar(), termin.getID());
            //erneure zeitstempel und editorID
            serverDaten.datenbank.incTimestemp(termin.getID());
            serverDaten.datenbank.updateEditorID(termin.getID(), userID);

            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try{ 
                        child.getServerStub().changeEditierrechte(termin, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                    } catch (BenutzerException ex){}
                }
            }    
                      
        }
        else{
            this.serverDaten.parent.getServerStub().changeEditierrechteDB(termin, userID);
        }
    }
    
    /**
     * 
     * @param termin
     * @param serverID
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeEditierrechte(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditierbar(termin.getEditierbar(), username);                   
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditorID(sitzung.getEingeloggterBenutzer().getUserID());               
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().changeEditierrechte(termin, serverID, username);
            }           
        }
    }
    
    @Override
    public void changeTerminDB(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            //trage aktuallisierte Daten ein
            serverDaten.datenbank.changeTerminbeginn(termin.getID(), termin.getBeginn());
            serverDaten.datenbank.changeTerminende(termin.getID(), termin.getEnde());
            serverDaten.datenbank.changeTerminnotiz(termin.getID(), termin.getNotiz());
            serverDaten.datenbank.changeTerminort(termin.getID(), termin.getOrt());
            serverDaten.datenbank.changeTermintitel(termin.getID(), termin.getTitel());
            serverDaten.datenbank.changeTermindatum(termin.getID(), termin.getDatum());
            //erneure zeitstempel und editorID
            serverDaten.datenbank.incTimestemp(termin.getID());
            serverDaten.datenbank.updateEditorID(termin.getID(), userID);

            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try {
                        child.getServerStub().updateTermin(termin, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                    } catch (BenutzerException ex){}
                }
            }    
                      
        }
        else{
            this.serverDaten.parent.getServerStub().changeTerminDB(termin, userID);
        }
    }
    
    /**
     *  Methode, die einen gebenen Termin aktualisiert
     *  
     * @param termin Termin der zu updaten ist.
     * @param serverID
     * @param username
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void updateTermin(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //ändere Termin bei user (testet ob user editierrechte hat)
                        sitzung.getEingeloggterBenutzer().getTerminkalender().updateTermin(termin, termin.getOwner());
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditorID(sitzung.getEingeloggterBenutzer().getUserID());          
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().updateTermin(termin, serverID, username);
            }           
        }
    }
    
    @Override
    public void deleteTerminNichtOwner(Termin termin, String username, String text) throws RemoteException, SQLException, BenutzerException{
        Meldung meldung;
        int meldungsID;
        
        if(serverDaten.primitiveDaten.serverID.equals("0")){                       
            //suche in db nach termin           
            if(serverDaten.datenbank.terminExists(termin.getID())){               
                //Entferne Teilnehmer von dem Termin aus DB
                serverDaten.datenbank.removeTeilnehmer(username, termin.getID());
                
                //jedem Teilnehmer des Termins wird der Teilnehmer aus dem Termin entfernt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().removeTeilnehmer(termin.getID(), serverDaten.getServerIdByUsername(teilnehmer.getUsername()), username, meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().deleteTerminNichtOwner(termin, username, text);
        }
    }
    
    /**
     * 
     * @param termin
     * @param username
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteTerminAlsOwner(Termin termin, String username, String text) throws RemoteException, SQLException{
        Meldung meldung;
        int meldungsID;
        
        if(serverDaten.primitiveDaten.serverID.equals("0")){                       
            //suche in db nach termin           
            if(serverDaten.datenbank.terminExists(termin.getID())){               
                //Entferne Teilnehmer von dem Termin aus DB
                serverDaten.datenbank.deleteTermin(termin.getID());
                //Entferne alle Anfragen zu dem Termin
                serverDaten.datenbank.deleteAnfrageByTerminID(termin.getID());
                
                //jedem Teilnehmer des Termins wird der Termin entfernt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().removeTermin(termin.getID(), serverDaten.getServerIdByUsername(teilnehmer.getUsername()), username, meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().deleteTerminAlsOwner(termin, username, text);
        }
    }
    
    /**
     * 
     * @param terminID
     * @param username
     * @param serverID
     * @param meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void removeTeilnehmer(int terminID, String username, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //entfernt den Teilnehmer
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).removeTeilnehmer(username);
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().removeTeilnehmer(terminID, username, serverID, meldung);
            }           
        }
    }
    
    @Override
    public void removeTermin(int terminID, String username, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //entfernt den Teilnehmer
                        sitzung.getEingeloggterBenutzer().getTerminkalender().removeTerminByID(terminID);
                        //entfernt die Anfrage zu dem Termin (evtl)
                        sitzung.getEingeloggterBenutzer().deleteAnfrage(terminID);
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                    } catch (TerminException | BenutzerException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().removeTermin(terminID, username, serverID, meldung);
            }           
        }
    }
    
    @Override
    public void addTerminTeilnehmerDB(Termin termin, String username, String einlader) throws RemoteException, SQLException, BenutzerException{
        if(serverDaten.primitiveDaten.serverID.equals("0")){
            System.out.println(username);
            if(serverDaten.datenbank.userExists(username)){ 
                //suche in db nach termin           
                if(serverDaten.datenbank.terminExists(termin.getID())){
                    //Füge dem Termin den neuen Teilnehmer in der DB hinzu
                    serverDaten.datenbank.addTeilnehmer(termin.getID(), username);
                    //jedem Teilnehmer des Termins wird der neue Teilnehmer dem Termin hinzugefügt
                    for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                        for(Verbindung child : this.serverDaten.childConnection){
                            try{   
                                child.getServerStub().addTeilnehmer(termin.getID(), teilnehmer.getUsername(), username, serverDaten.getServerIdByUsername(teilnehmer.getUsername()));
                            } catch (BenutzerException ex){}
                        }
                    }  
                    
                    String text = einlader + " lädt sie zu einem Termin am ";
                    Anfrage anfrage = new Anfrage(text, termin, einlader, this.serverDaten.datenbank.getMeldungsCounter());
                    //Füge der DB die Anfrage hinzu
                    serverDaten.datenbank.addAnfrage(username, termin.getID(), einlader, text);                   

                    //Füge dem neuen Teilnehmer
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().addTermin(anfrage, serverDaten.getServerIdByUsername(username), username);
                        } catch (BenutzerException ex){}
                    }                    
                }
            }              
        }
        else{
            this.serverDaten.parent.getServerStub().addTerminTeilnehmerDB(termin, username, einlader);
        }
    }
    
    
    //asd
    /**
     * Methode fügt allen Teilnehmern des Termins den neuen Teilnehmer hinzu
     * 
     * @param terminID id des termins
     * @param username username des hinzuzufügenden teilnehmers
     * @param kontakt
     * @param serverID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void addTeilnehmer(int terminID, String username, String kontakt, String serverID) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        System.out.println(serverID + " server: " + serverDaten.primitiveDaten.serverID);
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //ändere Termin bei user (testet ob user editierrechte hat)
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).addTeilnehmer(kontakt);                        
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().addTeilnehmer(terminID, username, kontakt, serverID);
            }           
        }
    }
    
    /**
     *
     * @param serverID
     * @param username
     * @param anfrage Anfrage in Form einer Meldung
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void addTermin(Anfrage anfrage, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    //füge dem user den termin hinzu
                    sitzung.getEingeloggterBenutzer().getTerminkalender().addTermin(anfrage.getTermin());
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(anfrage.getTermin().getID()).addTeilnehmer(username);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //füge dem user die anfrage hinzu
                    sitzung.getEingeloggterBenutzer().addAnfrage(anfrage);     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().addTermin(anfrage, serverID, username);
            }           
        }
    }
      
    /**
     * Methode um den Status, ob ein User teilnimmt oder nicht, zu setzen
     * 
     * @param termin termin
     * @param username identifiziert den user
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void teilnehmerNimmtTeil(Termin termin, String username, String text) throws RemoteException, SQLException{
        Meldung meldung;
        int meldungsID;
        
        if(serverDaten.primitiveDaten.serverID.equals("0")){                       
            //suche in db nach termin           
            if(serverDaten.datenbank.terminExists(termin.getID())){               
                //Entferne Teilnehmer von dem Termin aus DB
                serverDaten.datenbank.nimmtTeil(termin.getID(), username);
                //Anfrage aus DB löschen
                serverDaten.datenbank.removeAnfrageForUserByTerminID(termin.getID(), username);
                
                //jedem Teilnehmer des Termins wird der Teilnehmer auf nimmt Teil gesetzt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().setNimmtTeil(termin.getID(), serverDaten.getServerIdByUsername(teilnehmer.getUsername()), username, meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().teilnehmerNimmtTeil(termin, username, text);
        }
    }
   
    @Override
    public void setNimmtTeil(int terminID, String username, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //setzt teilnehmer nimmt teil
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).changeTeilnehmerNimmtTeil(username);
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().setNimmtTeil(terminID, username, serverID, meldung);
            }           
        }
    }
}
