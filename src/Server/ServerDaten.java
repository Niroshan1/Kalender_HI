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
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter; 
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
    
    public ServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{    
        this.parent = null;
        this.leftchild = null;
        this.rightchild = null;
        this.ownIP = args[0];
        this.parentIP = args[1];
        datenbank = null;      
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
                //Speicherung von ServerIP und Serverid
                System.out.println( "parentIP + ownIP  gespeichert");

                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this, this.parent).start();                
            }
            else{
                //TODO: werfe Fehler
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
    }
    
    public void serverDatenSpeicherung(){
        PrintWriter pWriter = null;
        try {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("serverIP.txt")));
            pWriter.println(ownIP + parentIP);
        } catch (IOException ioe) {
        } finally {
            if (pWriter != null){
                pWriter.flush();
                pWriter.close();
            }
        } 
    }
}

