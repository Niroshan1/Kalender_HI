/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
import Utilities.DatenbankException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.portable.RemarshalException;

/**
 *
 * @author timtim
 */
public class ServerDaten {

    public Verbindung parent;
    public DBHandler datenbank;
    public DBHandler tmpDatenbank;
    public final String ownIP;
    public final String parentIP;
    public String serverID;
    public final String[] childCount;
    public Verbindung[] childConnection;
    public int kalenderAnzahl;

    //Hier wird der ID und IP von Kind mit kleinste Kalender anzahl gespeichert
    //public String serverIDKind;
    //public String serverIPKind;

    public ServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {

        this.parent = null;
        this.ownIP = args[0];
        this.parentIP = args[1];
        this.childCount = new String[10];
        this.childConnection = new Verbindung[10];
        //this.serverIDKind = null;
        //this.serverIPKind = null;

        if (parentIP.equals("root")) {
            this.serverID = "0";
        } else {
            this.serverID = null;
        }

        datenbank = null;
        tmpDatenbank = null;
        kalenderAnzahl = 0;
    }

    /**
     * Methode, die anhand einer serverID den nächstbesten Server findet.
     *
     * @param serverID Die ID des gesuchten Servers
     * @return serverIP , ownIP Bei gleicher serverID ist es die eigene IP
     */
    public String findServer(String serverID) {
        int length = this.serverID.length();

        //Ist die übergebene serverID kleiner der eigenen steht der gesuchte Server im Baum weiter oben.
        if (serverID.length() < length) {
            return parentIP;
        }

        //Triviale Lösung
        if (serverID.equals(this.serverID)) {
            return ownIP;
        } else if (serverID.length() == length) //Der gesuchte Server ist in einem anderen Zweig.
        {
            return parentIP;
        } else //Die obigen checks schlugen fehl, also ist der gesuchte Server ein Kind
        {
            return childCount[Character.getNumericValue(serverID.charAt(this.serverID.length()))];
        }
    }

    /**
     * baut Verbindungen zu einem anderen Server auf
     *
     * @throws RemoteException
     * @throws IOException
     */
    public void connectToParent() throws IOException {
        ServerStub serverStub;
        Registry registry;

        try {
            //baut Verbindung zu Server auf
            registry = LocateRegistry.getRegistry(parentIP, 1100);
            serverStub = (ServerStub) registry.lookup("ServerStub");

            //lässt anderen Server Verbindung zu diesem aufbauen
            if ((this.serverID = serverStub.initConnection(this.ownIP)) != null) {
                //fügt Verbindung zur Liste der Verbindungen hinzu
                this.parent = new Verbindung(serverStub, parentIP);

                //Ausgabe im Terminal
                System.out.println("LOG * ---> Verbindung zu Parent " + parentIP + " hergestellt!");
                System.out.println("LOG * ---> Server wurde die ServerID: " + this.serverID + " zugewiesen!");
                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this, this.parent).start();

            } else {
                //TODO: Fehlermeldung anpassen && Server beenden lassen
                System.out.println("neue ServerID wurde nicht erhalten / Max Connection?");
            }

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ladeDatenbank() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        datenbank = new DBHandler();
        datenbank.getConnection();
    }

    public String serverKalenderAnzahlBewerten() throws RemoteException, RemarshalException, SQLException, DatenbankException {
        int anzahlKalender = 0;
        int kleineKalenderAnzahl = 0;
        int verbindungID = 0;

        // Prueft ob root weniger Kalender hat
        for (int i = 0; i < this.childConnection.length; i++) {

            if (this.childConnection[i] != null) {
                anzahlKalender = this.childConnection[i].getServerStub().getkalenderAnzahl();

                if (kleineKalenderAnzahl >= anzahlKalender) {
                    kleineKalenderAnzahl = anzahlKalender;
                    verbindungID = i;
                }
            }
            
            
        }

        if (this.childConnection[verbindungID] != null) {
            if (kleineKalenderAnzahl >= this.kalenderAnzahl) {
                return this.childConnection[verbindungID].getServerStub().getServerID();
            }
        }

        return this.serverID;
    }
    /*public void serverDatenSpeicherung(){
        PrintWriter pWriter = null;
        try {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("serverIP.txt")));
            pWriter.println(ownIP + " " + parentIP);
        } catch (IOException ioe) {
        } finally {
            if (pWriter != null){
                pWriter.flush();
                pWriter.close();
            }
        } 
    }*/
}
