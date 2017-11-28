/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiconnection;

import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author timtim
 */
public class Server {

    private LinkedList<ServerStub> connectionList;
    private LinkedList<String> onlineServerListe;
    
    public Server(){
        this.connectionList = new LinkedList<>();
        this.onlineServerListe = new LinkedList<>();
    }

    void start(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
  
        initServerStub();
        initClientStub();
       
        getOnlineServerListe();
        connectToServers();
        
        hilfsfunktion(args);
        
        System.out.println("Server laeuft!");
    }

    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(connectionList);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub);
    }

    private void initClientStub() throws RemoteException, AlreadyBoundException{
        ClientStubImpl clientLauncher = new ClientStubImpl();   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
    }

    private void getOnlineServerListe() {
        // lese die Server aus der Datei alleServerListe.txt (o.ä.)
        // versuche verbindung (rmi) mit diesen der reihe nach aufzubauen, bis
        // eine Verbindung erzeugt werden kann
        // dann rufe dort die Methode getOnlineServerList() auf
        
        //onlineServerListe = stub.getOnlineServerList();
    }

    private void connectToServers() {
        // int anzahlVerbindungen = zufallswert zwischen 2 und 5 (?)
        // baue anzahlVerbindungen Verbindungen zu zufälligen Servern auf
        // zufälligen Server könnten sein: 
        //      - komplett zufällig
        //      - bester pings
        //      - server mit wenigsten verbindungen
        // nach jeder erstellten verbindung muss beim anderen server die Methode
        // reconnect aufgerufen werden & die verbindung in die connectionList 
        // geadded werden (der stub muss da rein)
        //
        // falls nur ein server online, dann nur eine Verbindung erstellen!!!
    }

    private void hilfsfunktion(String[] args) throws RemoteException, NotBoundException {
        if(args.length > 0){
            String ip = args[0];

            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(stub); 
            stub.reconnect(ip, 1100);
            
            System.out.println("Hallo");
        }
    }
    
}
