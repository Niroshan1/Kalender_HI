/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DBHandler;
import Server.Utilities.Sitzung;
import Server.Utilities.UserAnServer;
import Server.Utilities.Verbindung;
import ServerThreads.VerbindungstestsParentThread;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class ServerDaten {

    //Verbindungen zu parent & childs
    public Verbindung parent;
    public LinkedList<Verbindung> childConnection;
    //Datenbank
    public DBHandler datenbank;  
       
    //Liste mit aktiven Sitzungen (eingeloggte User des Servers)
    public final LinkedList<Sitzung> aktiveSitzungen; 
    public PrimitiveServerDaten primitiveDaten;
        
    public final LinkedList<UserAnServer> userAnServerListe; 

    public ServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        this.parent = null;
        this.childConnection = new LinkedList<>();
        this.aktiveSitzungen = new LinkedList<>();

        if (args[1].equals("root")) {
            primitiveDaten = new PrimitiveServerDaten(args[0], "0");
            datenbank = new DBHandler(aktiveSitzungen, childConnection, primitiveDaten);
            datenbank.getConnection(0); 
            userAnServerListe = new LinkedList<>();
        } else {
            primitiveDaten = new PrimitiveServerDaten(args[0], null);
            datenbank = null; 
            userAnServerListe = null;
        }
        
    }

    /**
     * baut Verbindungen zu einem anderen Server auf
     *
     * @param parentIP
     * @throws RemoteException
     * @throws IOException
     */
    public void connectToParent(String parentIP) throws IOException {
        ServerStub serverStub;
        Registry registry;

        try {
            //baut Verbindung zu Parent auf
            registry = LocateRegistry.getRegistry(parentIP, 1100);
            serverStub = (ServerStub) registry.lookup("ServerStub");

            //lässt anderen Server Verbindung zu diesem aufbauen
            this.primitiveDaten.serverID = serverStub.initConnection(this.primitiveDaten.ownIP);
            
            //fügt Verbindung zur Liste der Verbindungen hinzu
            this.parent = new Verbindung(serverStub, parentIP, serverStub.getServerID());

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu Parent " + parent.getIP() + " hergestellt!");
            System.out.println("LOG * ---> Server wurde die ServerID:" + this.primitiveDaten.serverID + " zugewiesen!");
            
            //Starte Threads, die die Verbindung zu anderen Servern testen
            new VerbindungstestsParentThread(this, this.parent).start();
                 
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerDaten.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
