package Server;

import Server.Utilities.Sitzung;
import Server.Utilities.Verbindung;
import ServerThreads.VerbindungstestsChildsThread;
import Utilities.Anfrage;
import Utilities.BenutzerException;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
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
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeEditierrechte(String originIP, int requestCounter, Termin termin) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(termin.getID())){
                serverDaten.datenbank.updateTermin(termin);

                for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditierbar(termin.getEditierbar(), termin.getOwner());
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setTimestemp(termin.getTimestemp());                      
                    } catch (TerminException ex) {}
                }
            }
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().updateTermin(originIP, requestCounter, termin);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }  
        }
    }
    
    /**
     *  Methode, die einen gebenen Termin aktualisiert
     *  
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert.
     * @param termin Termin der zu updaten ist.
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void updateTermin(String originIP, int requestCounter, Termin termin) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(termin.getID())){
                serverDaten.datenbank.updateTermin(termin);
                //ändere termin auf server
                for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                    try {
                        if(sitzung.getEingeloggterBenutzer().getTerminkalender().updateTermin(termin, termin.getOwner(), sitzung.getEingeloggterBenutzer().getUserID())){
                            sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setTimestemp(termin.getTimestemp());
                        }
                    } catch (TerminException ex) {}
                }
            }
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().updateTermin(originIP, requestCounter, termin);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }  
        }
    }
    
    /**
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @param meldungsText
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteTermin(String originIP, int requestCounter, Termin termin, String meldungsText) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
       
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(termin.getID())){
                //lösche aus db
                serverDaten.datenbank.deleteTermin(termin.getID());
                //lösche von server
                for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    if(serverDaten.datenbank.userExists(teilnehmer.getUsername())){
                        int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), meldungsText, false);
                        for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                            if(teilnehmer.getUsername().equals(sitzung.getEingeloggterBenutzer().getUsername())){
                                try {
                                    sitzung.getEingeloggterBenutzer().getTerminkalender().removeTerminByID(termin.getID());
                                    sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(meldungsText, meldungsID));
                                    //und entferne falls vorhanden die Anfrage zu diesem Termin
                                    sitzung.getEingeloggterBenutzer().deleteAnfrage(termin.getID());
                                }catch (TerminException | BenutzerException ex) {}
                            
                            }
                        }
                    }
                }
            }
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().deleteTermin(originIP, requestCounter, termin, meldungsText);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }  
        }
    }
    
    /**
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @param username
     * @param userID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void removeTeilnehmerFromTermin(String originIP, int requestCounter, Termin termin, String username, int userID) throws RemoteException, SQLException{
            String text = username 
                            + " nimmt nicht mehr an dem  Termin '" 
                            + termin.getTitel()
                            + "' am "
                            + termin.getDatum().toString()
                            + " teil";
        // war die Anfrage schonmal hier       
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(termin.getID())){
                //entferne teilnehmer von db
                serverDaten.datenbank.removeTeilnehmer(username, termin.getID());
                //entferne teilnehmer vom server
                for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    if(serverDaten.datenbank.userExists(teilnehmer.getUsername())){
                        int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                        for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                            if(teilnehmer.getUsername().equals(sitzung.getEingeloggterBenutzer().getUsername())){
                                try {
                                    sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).removeTeilnehmer(username);
                                    sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(text, meldungsID));
                                }catch (TerminException ex) {}                           
                            }
                        }
                    }
                }
            }
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().removeTeilnehmerFromTermin(originIP, requestCounter, termin, username, userID);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }  
        }
    }
    
    
    /**
     * Methode fügt allen Teilnehmern des Termins den neuen Teilnehmer hinzu
     * 
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert
     * @param terminID id des termins
     * @param userID id des hinzuzufügenden teilnehmers
     * @param username username des hinzuzufügenden teilnehmers
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void addTeilnehmer(String originIP, int requestCounter, int terminID, int userID, String username) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(terminID)){
                serverDaten.datenbank.addTeilnehmer(terminID, username);
                //suche auf server nach termin
                for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                    try {
                        //teste ob termin vorhanden und füge teilnehmer hinzu
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).addTeilnehmer(username);     
                    } catch (TerminException ex) {}
                }
            }
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().addTeilnehmer(originIP, requestCounter, terminID, userID, username);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }  
        }
    }
    
    /**
     *
     * @param originIP Die IP-Adresse des Initiators der Flooding Anfrage
     * @param requestCounter identifizert die request eindeutig (eines servers)
     * @param userID  UserID des Users bei dem der Termin hinzugefügt werden soll
     * @param anfrage Anfrage in Form einer Meldung
     * @param sendername Name des Senders der Anfrage wenn per flooding gesendet wird
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void addTermin(String originIP, int requestCounter, int userID, Anfrage anfrage, String sendername) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.userExists(userID)){   
                if(!serverDaten.datenbank.terminExists(anfrage.getTermin().getID())){                   
                    //füge Termin der DB hinzu                   
                    serverDaten.datenbank.addExistingTermin(anfrage.getTermin());
                }
                //füge die anfrage der db hinzu
                int meldungsID = serverDaten.datenbank.addAnfrage(serverDaten.datenbank.getUsernameById(userID), anfrage.getTermin().getID(), sendername, anfrage.getText());

                //suche auf server nach dem user
                for(Sitzung sitzung : serverDaten.aktiveSitzungen){                   
                    if(sitzung.getEingeloggterBenutzer().getUserID() == userID){
                        //füge dem user den termin hinzu
                        sitzung.getEingeloggterBenutzer().getTerminkalender().addTermin(anfrage.getTermin());
                        //füge dem user die anfrage hinzu
                        sitzung.getEingeloggterBenutzer().addAnfrage(new Anfrage(anfrage.text, anfrage.getTermin(), anfrage.getAbsender(), meldungsID));
                    }     
                }
            } 
            else{
                //Flooding weiterleitung
                for(Verbindung connection : serverDaten.connectionList){             
                    new Thread(() ->{
                        try {
                            connection.getServerStub().addTermin(originIP, requestCounter, userID, anfrage, sendername);
                        } catch (RemoteException | SQLException ex) { }
                    }).start();
                } 
            }
        }
    }
    
    /**
     * Methode um den Status, ob ein User teilnimmt oder nicht, zu setzen
     * 
     * @param originIP Die IP-Adresse des Initiators der Flooding Anfrage
     * @param requestCounter identifizert die request eindeutig (eines servers)
     * @param termin termin
     * @param username identifiziert den user
     * @param status bestimmt ob user teilnimmt (true) oder nicht (false)
     * @param meldungstext text der meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void teilnehmerChangeStatus(String originIP, int requestCounter, Termin termin, String username, boolean status, String meldungstext) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach termin
            if(serverDaten.datenbank.terminExists(termin.getID())){   
                //aktualisiere Terminkalender in DB (nimmtTeil = 1 bzw lösche Eintrag von username)
                if(status){
                    serverDaten.datenbank.nimmtTeil(termin.getID(), username);
                }
                else{
                    serverDaten.datenbank.removeTeilnehmer(username, termin.getID());
                }
                
                //für jeden teilnehmer des termins
                for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    //ist teilnehmer auf db?
                    if(serverDaten.datenbank.userExists(teilnehmer.getUsername())){
                        //generiere dem Teilnehmer eine meldung
                        int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), meldungstext, false);
                        //suche auf server nach dem termin
                        for(Sitzung sitzung : serverDaten.aktiveSitzungen){ 
                            if(sitzung.getEingeloggterBenutzer().getUsername().equals(teilnehmer.getUsername())){
                                try {
                                //test ob eingeloggter user zu termin eingeladen ist oder daran teilnimmt
                                Termin terminAufServer = sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID());
                                
                                if(status){
                                    //füge den anderen teilnehmern die info, dass ... an dem termin teilnimmt hinzu
                                    terminAufServer.changeTeilnehmerNimmtTeil(username);
                                }
                                else{
                                    //entferne teilnehmer von server
                                    terminAufServer.removeTeilnehmer(username);
                                }
                                //füge den anderen teilnehmern die eingeloggt sind die meldung hinzu
                                sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(meldungstext, meldungsID));
                            } catch (TerminException ex) { }  
                            }  
                        }
                    }
                }
            }            
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().teilnehmerChangeStatus(originIP, requestCounter, termin, username, status, meldungstext);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }            
        }
    }
    
    /**
     * testet ob Anfrage bereits behandelt wurde, wenn ja: gibt true zurück,
     * wenn nein: gibt false zurück und speichert den request in der Liste ab
     * 
     * @param originIP ip des ursprungs der anfrage
     * @param requestCounter identifizert die request eindeutig (eines servers)
     * @return 
     */
    private boolean checkRequest(String originIP, int requestCounter){
        //gibt es von der IP bereits Anfragen
        if (serverDaten.requestTable.containsKey(originIP)){
            //Wenn ja welche Anfragen 
            if(serverDaten.requestTable.get(originIP).contains(requestCounter)){
                return true;
            }
            else{
                serverDaten.requestTable.get(originIP).add(requestCounter);
                return false;
            }
        }
        else{
            serverDaten.requestTable.put(originIP, new LinkedList<>());
            serverDaten.requestTable.get(originIP).add(requestCounter);
            return false;
        }
    }
}
