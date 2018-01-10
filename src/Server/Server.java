/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DatenbankException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class Server {

    private final ServerDaten serverDaten;
    private final String[] args;
    
    public Server(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{       
        this.serverDaten = new ServerDaten(args[0]);
        this.args = args;
        System.setProperty("java.rmi.server.hostname", args[0]);
    }

    /**
     * Startet den Server und ruft alle Methoden auf, die dazu notwendig sind
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws SQLException
     * @throws DatenbankException
     * @throws IOException 
     */
    public void start() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException, ClassNotFoundException, NoSuchAlgorithmException{
        
        System.out.println("LOG * Starte Server");
        System.out.println("LOG * Server-IP: " + serverDaten.ownIP);
        System.out.println("LOG * ");
              
        //initialisiere Stubs für Server & Clients
        System.out.println("LOG * ");
        initServerStub();
        initClientStub();
        
        //baut Verbindung zu Parent auf
        if(!args[1].equals("root")){
            connectToParent();
            this.serverDaten.ladeDatenbankFromParent();
        }
        else{
            this.serverDaten.ladeDatenbank();
        }

        System.out.println("LOG * ");
        System.out.println("LOG * Server laeuft!");
        System.out.println("---------------------------------------------");
    }

    /**
     * initialisiert den Stub für die Server
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException 
     */
    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(serverDaten);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub);
        System.out.println("LOG * ServerStub initialisiert!");
    }

    /**
     * initialisiert den Stub für die Clients
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws SQLException
     * @throws DatenbankException 
     */
    private void initClientStub() throws RemoteException, AlreadyBoundException, SQLException, DatenbankException{
        ClientStubImpl clientLauncher = new ClientStubImpl(this.serverDaten.datenbank);   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
        System.out.println("LOG * ClientStub initialisiert!");
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
        String parentIP = args[1];
        
        try {
            //baut Verbindung zu Server auf
            registry = LocateRegistry.getRegistry(parentIP, 1100);
            serverStub = (ServerStub) registry.lookup("ServerStub");

            //lässt anderen Server Verbindung zu diesem aufbauen
            if(serverStub.initConnection(this.serverDaten.ownIP)){
                //TODO: werfe Fehler
            }

            //fügt Verbindung zur Liste der Verbindungen hinzu
            this.serverDaten.parent = new Verbindung(serverStub, parentIP);

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu Server " + parentIP + " hergestellt!");

            //Starte Threads, die die Verbindung zu anderen Servern testen
            new VerbindungstestsThread(this.serverDaten, this.serverDaten.parent).start();   

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }                           
    }
   
    
}
