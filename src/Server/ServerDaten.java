/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    public Verbindung parent;
    public Verbindung leftchild;
    public Verbindung rightchild;
    public DBHandler datenbank;
    public final String ownIP;
    public final String parentIP;
    public String serverID;
    public final String[] childCount;
    public Verbindung[] childConnection;


    public ServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{

        this.parent = null;
        //this.leftchild = null;
        //this.rightchild = null;
        this.ownIP = args[0];
        this.parentIP = args[1];
        this.childCount = new String[10];
        this.childConnection = new Verbindung[10];

        if (parentIP.equals("root"))
            this.serverID = "0";
        else
            this.serverID = null;

        datenbank = null;
    }

    /**
     * Methode um anhand der parentIP die serverID zu bekommen.
     * @param parentIP
     * @return
     */

    private String getID(String parentIP){
        for(int i = 0; i <= childCount.length; i++){
            if(childCount[i].equals(parentIP)){

            }
        }
        return serverID;
    }

    /**
     * Methode, die anhand einer serverID den nächstbesten Server findet.
     * @param serverID Die ID des gesuchten Servers
     * @return serverIP , ownIP Bei gleicher serverID ist es die eigene IP
     */
    private String findServer(String serverID) {
        int length = this.serverID.length();

        //Ist die übergebene serverID kleiner der eigenen steht der gesuchte Server im Baum weiter oben.
        if (serverID.length() < length) {
            return parentIP;
        }

        //Triviale Lösung
        if (serverID.equals(this.serverID))
            return ownIP;
        else if (serverID.length() == length) //Der gesuchte Server ist in einem anderen Zweig.
            return parentIP;
        else //Die obigen checks schlugen fehl, also ist der gesuchte Server ein Kind
            return childCount[Character.getNumericValue(serverID.charAt(this.serverID.length()))];
    }
    /**
     * baut Verbindungen zu einem anderen Server auf
     * 
     * @throws RemoteException
     * @throws IOException 
     */
    public void connectToParent() throws IOException{
        ServerStub serverStub;
        Registry registry;

        try {
            //baut Verbindung zu Server auf
            registry = LocateRegistry.getRegistry(parentIP, 1100);
            serverStub = (ServerStub) registry.lookup("ServerStub");

            //lässt anderen Server Verbindung zu diesem aufbauen
            if(serverStub.initConnection(this.ownIP)){
                //fügt Verbindung zur Liste der Verbindungen hinzu
                this.parent = new Verbindung(serverStub, parentIP);

                //Ausgabe im Terminal
                System.out.println("LOG * ---> Verbindung zu Parent " + parentIP + " hergestellt!");

                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this , this.parent).start();

            }
            else{
                //TODO: Fehlermeldung anpassen && Server beenden lassen
                System.out.println("werfe fehler");
            }

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ladeDatenbank() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        datenbank = new DBHandler();
        datenbank.getConnection();
    }

    void ladeDatenbankFromParent() {
        //TODO: lade DB von Parent (mit Stub-Methode)
        //this.parent.ladeDB();
    }



 
}
