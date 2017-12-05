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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
    private LinkedList<String> onlineServerList;
    private final DBHandler datenbank;
    
    public Server() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.connectionList = new LinkedList<>();
        this.onlineServerList = new LinkedList<>();
        
        datenbank = new DBHandler(); 
        datenbank.getConnection();
    }

    /**
     * Startet den Server und ruft alle Methoden auf, die dazu notwendig sind
     * 
     * @param args
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws SQLException
     * @throws DatenbankException
     * @throws IOException 
     */
    public void start(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException{
  
        getOnlineServerListe();
        
        //naechste zeile nur zum testen notwendig
        this.onlineServerList.add("192.168.44.7");
        
        //this.onlineServerList.add(getOwnIP());
        //TODO: anderen Servern diesen ihrer Liste hinzufuegen
        
        System.out.println("");
        initServerStub();
        initClientStub();

        if(this.onlineServerList.size() > 1){
            connectToServers();
        }

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
        File file = new File("C:\\Users\\timtim\\Documents\\NetBeansProjects\\P2P\\src\\data\\serverlist.txt");
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
        while(counter < 2){
            try {
                foreignIP = findBestServerToConnectWith();
                if(foreignIP.equals("Kein Server mehr verfügbar!")){
                    System.out.println("---> Kein Server mehr verfügbar!");
                    counter = 2;
                }
                else{
                    System.out.println("Versuch zu " + foreignIP);
                    //baut Verbindung zu anderem Server auf
                    registry = LocateRegistry.getRegistry(foreignIP, 1100);
                    stub = (ServerStub) registry.lookup("ServerStub");
                    //lässt anderen Server Verbindung zu diesem aufbauen
                    stub.initConnection(getOwnIP(), 1100);
                    //fügt Verbindung zur Liste der Verbindungen hinzu
                    this.connectionList.add(new Verbindung(stub, foreignIP, 1100));
                    System.out.println("---> Verbindung zu Server " + foreignIP + " hergestellt!");

                    //falls bisher nur ein Server online, dann muss nur eine Verbindung aufgebaut werden
                    if(this.onlineServerList.size() == 2){
                        counter++;
                    }
                    counter++;
                }
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
     * liefert die globale ip des geräts
     * 
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    private String getOwnIP() throws MalformedURLException, IOException{      
        URL url = new URL("http://bot.whatismyipaddress.com");
        BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));
        return sc.readLine().trim();
    }
    
    /**
     * findet den Server aus der onlineServerList, mit dem noch keine Verbindung besteht
     * und der die Beste Latenz hat
     * 
     * @return 
     */
    private String findBestServerToConnectWith(){
        String bestServer = "Kein Server mehr verfügbar!";
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
