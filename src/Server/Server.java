/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DBHandler;
import Utilities.DatenbankException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class Server {

    private LinkedList<Verbindung> connectionList;
    private LinkedList<String> onlineServerListe;
    private final DBHandler datenbank;
    
    public Server(){
        this.connectionList = new LinkedList<>();
        this.onlineServerListe = new LinkedList<>();
        
        datenbank = new DBHandler(); 
        try {
            datenbank.getConnection();
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ClientStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException{
  
        initServerStub();
        initClientStub();
       
        getOnlineServerListe();
        connectToServers();
        
        //kommt später weg
        hilfsfunktion(args);
        
        starteThreadsMitVerbindungstests();
        
        System.out.println("Server laeuft!");
    }

    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(connectionList, onlineServerListe, datenbank);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1101);
        serverRegistry.bind("ServerStub", serverStub);
    }

    private void initClientStub() throws RemoteException, AlreadyBoundException, SQLException, DatenbankException{
        ClientStubImpl clientLauncher = new ClientStubImpl(datenbank);   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1199);
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
        // zufaellige Anzahl an Verbindungen
        int anzahlVerbindungen = (int)(Math.random() * 5) + 1;
        
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
        System.setProperty("java.rmi.server.hostname", "95.88.91.227");
        
       // if(args.length > 0){
            //String ip = args[0];

            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(new Verbindung(stub, "localhost", 1100)); 
            stub.initConnection("localhost", 1100);
            
            System.out.println("Hallo");
        //}
    }

    private void starteThreadsMitVerbindungstests() {
        // diese Methode soll für jeder Verbindung einen Thread starten
        // in diesem Thread wird alle x Sekunden getestet ob der andere Server 
        // noch erreichbar ist. Dazu wird die Methode Ping (vom ServerStub) verwendet
        
        // benutze counter = 2
        // alle x Sekunden wird der Counter 1 runtergezählt
        // bekommt man eine Antwort beim pingen, dann wird der Counter wird auf 2 gesetzt
        // ist der counter bei 0, wird eine anfrage in das netzwerkgeschickt, 
        // ob der server von anderen erreicht werden kann (Methode vom ServerStub: isServerReachable)
        // falls niemand ihn erreichen
    }
    
}
