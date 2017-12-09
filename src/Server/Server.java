/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
import Utilities.DatenbankException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class Server {

    private final ServerDaten serverDaten;
    
    public Server(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{       
        this.serverDaten = new ServerDaten(args[0]);
        
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
    public void start() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException{
        
        System.out.println("LOG * Starte Server");
        System.out.println("LOG * Server-IP: " + serverDaten.ownIP);
        System.out.println("LOG * ");
        
        //erhalte Liste mit allen Servern die online sind
        getOnlineServerListe();
               
        //initialisiere Stubs für Server & Clients
        System.out.println("LOG * ");
        initServerStub();
        initClientStub();

        //baue bis zu 2 dauerhafte Verbindungen zu anderen Servern auf
        if(this.serverDaten.onlineServerList.size() > 0){
            connectToServers();
        }

        //füge dich selbst der Liste hinzu
        this.serverDaten.onlineServerList.add(this.serverDaten.ownIP);
        //lass die anderen Server dich in ihre Liste hinzufügen (mit Flooding)
        for(Verbindung verbindung : this.serverDaten.connectionList){
            new Thread(() -> {
                try {
                    verbindung.getServerStub().updateOnlineServerList(this.serverDaten.ownIP, this.serverDaten.ownIP);
                } catch (RemoteException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();
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
     * baut eine Verbindung zu einem Server auf und lädt die onlineServerList von diesem
     */
    private void getOnlineServerListe() {
        boolean successfulConnection = false;
        String terminalAusgabe = "LOG * ---> kein Server gefunden!";
        
        BufferedReader bufferedReader; 
        String line; 
        File file = new File("src\\data\\serverlist.txt");
        Registry registry;
        ServerStub stub;  
        
        System.out.println("LOG * Versuche Verbindung zu einem Server herzustellen um OnlineServerList zu erhalten");
        try { 
            bufferedReader = new BufferedReader(new FileReader(file)); 
            while ((line = bufferedReader.readLine()) != null && !successfulConnection) {                
                try { 
                    registry = LocateRegistry.getRegistry(line, 1100);
                    stub = (ServerStub) registry.lookup("ServerStub"); 
                    successfulConnection = true;
                    this.serverDaten.onlineServerList = stub.getOnlineServerList();
                    terminalAusgabe = "LOG * ---> OnlineServerList von " + line + " erhalten";
                } catch (RemoteException | NotBoundException e) {
                    System.out.println("LOG * ~~~ " + line + " nicht erreichbar!");
                }
            } 
            bufferedReader.close(); 
            System.out.println(terminalAusgabe);
        } catch (IOException ex) { 
            //MAC-Pfad
            file = new File("src/data/serverlist.txt");
            
            try { 
            bufferedReader = new BufferedReader(new FileReader(file)); 
            while ((line = bufferedReader.readLine()) != null && !successfulConnection) {                
                try { 
                    registry = LocateRegistry.getRegistry(line, 1100);
                    stub = (ServerStub) registry.lookup("ServerStub"); 
                    successfulConnection = true;
                    this.serverDaten.onlineServerList = stub.getOnlineServerList();
                    terminalAusgabe = "LOG * ---> OnlineServerList von " + line + " erhalten";
                } catch (RemoteException | NotBoundException e) {
                    System.out.println("LOG * ~~~ " + line + " nicht erreichbar!");
                }
            } 
            bufferedReader.close(); 
            System.out.println(terminalAusgabe);
            } catch (IOException ex1) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
            }   
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
        long ping;
        boolean skip;
        
        long startZeit;
        long endZeit;
        
        Registry registry;
        ServerStub stubTmp;
        
        Verbindung verbindung;
        ServerStub stub = null;
        String bestServerIP = "";
        
        System.out.println("LOG * ");
        System.out.println("LOG * Versuch bis zu 2 dauerhafte Verbindungen aufzubauen");
        
        if(this.serverDaten.onlineServerList.isEmpty()){
            counter++;
        }
        if(this.serverDaten.onlineServerList.size() == 1){
            counter++;
        }
        
        while(counter < 2){
            try {
                ping = 10000000;
                for(String serverIP : this.serverDaten.onlineServerList){
                    skip = false;
                    for(Verbindung verbundenerServer : this.serverDaten.connectionList){
                        if(verbundenerServer.getIP().equals(serverIP)){
                            skip = true;
                        }
                    }
                    if(!skip){
                        registry = LocateRegistry.getRegistry(serverIP, 1100);
                        stubTmp = (ServerStub) registry.lookup("ServerStub"); 
                        
                        startZeit = new Date().getTime();
                        stubTmp.ping();
                        endZeit = new Date().getTime();
                        
                        if(ping > (endZeit - startZeit)){
                            ping = endZeit - startZeit;
                            stub = stubTmp;
                            bestServerIP = serverIP;
                        }  
                    }
                }   
                //lässt anderen Server Verbindung zu diesem aufbauen
                stub.initConnection(this.serverDaten.ownIP);
                
                //fügt Verbindung zur Liste der Verbindungen hinzu
                verbindung = new Verbindung(stub, bestServerIP);
                this.serverDaten.connectionList.add(verbindung);
                System.out.println("LOG * ---> Verbindung zu Server " + bestServerIP + " hergestellt! (Ping = " + ping + ")");
                
                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this.serverDaten, verbindung).start();
                
                counter++;
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

}
