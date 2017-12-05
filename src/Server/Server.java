/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DBHandler;
import Utilities.DatenbankException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class Server {

    private final LinkedList<Verbindung> connectionList;
    private LinkedList<String> onlineServerList;
    private final DBHandler datenbank;
    private final String ownIP;
    
    public Server(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.connectionList = new LinkedList<>();
        this.onlineServerList = new LinkedList<>();
        this.ownIP = args[0];
        
        System.setProperty("java.rmi.server.hostname", this.ownIP);
        
        datenbank = new DBHandler(); 
        datenbank.getConnection();
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
    public void start() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException{
        
        System.out.println("Starte Server");
        System.out.println("Eigene IP: " + ownIP);
        
        //erhalte Liste mit allen Servern die online sind
        getOnlineServerListe();
               
        //initialisiere Stubs für Server & Clients
        System.out.println("");
        initServerStub();
        initClientStub();

        //baue bis zu 2 dauerhafte Verbindungen zu anderen Servern auf
        if(this.onlineServerList.size() > 0){
            connectToServers();
        }

        //füge dich selbst der Liste hinzu
        this.onlineServerList.add(this.ownIP);
        //lass die anderen Server dich in ihre Liste hinzufügen
        //(mit Flooding)
        for(Verbindung verbindung : this.connectionList){
                new FloodingThread(verbindung.getServerStub(), this.ownIP).start();
        }       
        
        //Starte Threads, die die Verbindung zu anderen Servern testen
        //TODO
        starteThreadsMitVerbindungstests();
        
        System.out.println("\nServer laeuft!");
    }

    /**
     * initialisiert den Stub für die Server
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException 
     */
    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(connectionList, onlineServerList, datenbank);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub);
        System.out.println("ServerStub initialisiert!");
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
        ClientStubImpl clientLauncher = new ClientStubImpl(datenbank);   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
        System.out.println("ClientStub initialisiert!");
    }

    /**
     * baut eine Verbindung zu einem Server auf und lädt die onlineServerList von diesem
     */
    private void getOnlineServerListe() {
        boolean successfulConnection = false;
        String terminalAusgabe = "---> kein Server gefunden!";
        
        BufferedReader bufferedReader; 
        String line; 
        File file = new File("src\\data\\serverlist.txt");
        Registry registry;
        ServerStub stub;  
        
        System.out.println("\nVersuche Verbindung zu einem Server herzustellen um OnlineServerList zu erhalten");
        try { 
            bufferedReader = new BufferedReader(new FileReader(file)); 
            while ((line = bufferedReader.readLine()) != null && !successfulConnection) {                
                try { 
                    registry = LocateRegistry.getRegistry(line, 1100);
                    stub = (ServerStub) registry.lookup("ServerStub"); 
                    successfulConnection = true;
                    this.onlineServerList = stub.getOnlineServerList();
                    terminalAusgabe = "---> OnlineServerList von " + line + " erhalten";
                } catch (RemoteException | NotBoundException e) {
                    System.out.println("*** " + line + " nicht erreichbar!");
                }
            } 
            bufferedReader.close(); 
            System.out.println(terminalAusgabe);
        } catch (IOException ex) { 
            Logger.getLogger(ClientStubImpl.class.getName()).log(Level.SEVERE, null, ex); 
        }         
    }

    /**
     * baut bis zu 2 Verbindungen zu anderen Servern auf
     * 
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IOException 
     */
    private void connectToServers() throws RemoteException, NotBoundException, IOException {
        int counter = 0;
        String foreignIP;
        
        Registry registry;
        ServerStub stub;
                           
        System.out.println("\nVersuche 2 dauerhafte Verbindungen herzustellen");
        
        if(this.onlineServerList.isEmpty()){
            counter++;
        }
        if(this.onlineServerList.size() == 1){
            counter++;
        }
        
        while(counter < 2){
            try {
                foreignIP = findBestServerToConnectWith();
                System.out.println("Versuch zu " + foreignIP);
                //baut Verbindung zu anderem Server auf
                registry = LocateRegistry.getRegistry(foreignIP, 1100);
                stub = (ServerStub) registry.lookup("ServerStub");
                //lässt anderen Server Verbindung zu diesem aufbauen
                stub.initConnection(this.ownIP, 1100);
                //fügt Verbindung zur Liste der Verbindungen hinzu
                this.connectionList.add(new Verbindung(stub, foreignIP, 1100));
                System.out.println("---> Verbindung zu Server " + foreignIP + " hergestellt!");
                
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
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
    
    /**
     * findet den Server aus der onlineServerList, mit dem noch keine Verbindung besteht
     * und der die Beste Latenz hat
     * 
     * @return 
     */
    private String findBestServerToConnectWith(){
        String bestServer = "";
        boolean skip;
        int ping = 10000;
        int tmpPing;
        
        for(String server : this.onlineServerList){
            skip = false;
            for(Verbindung verbundenerServer : this.connectionList){
                if(verbundenerServer.getIP().equals(server)){
                    skip = true;
                }
            }
            if(!skip){
                //teste ping zu server
                tmpPing = ping(server);
                if(ping > tmpPing){
                    bestServer = server;
                    ping = tmpPing;
                }
            }
        }
        return bestServer;
    }

    /**
     * Methode pinged einen Server an um die Latenz zu diesem ermitteln
     * 
     * @param server
     * @return 
     */
    private int ping(String ip) {
        //TODO
        return 0;
    }
}
