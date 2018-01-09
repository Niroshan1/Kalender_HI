/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    public final LinkedList<Verbindung> connectionList; 
    public final DBHandler datenbank;
    public final String ownIP;
    
    public ServerDaten(String ownIP) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.connectionList = new LinkedList<>();    
        this.ownIP = ownIP;
        datenbank = new DBHandler(); 
        datenbank.getConnection();        
    }
    
    /**
     * baut Verbindungen zu einem anderen Server auf
     * 
     * @return 
     * @throws RemoteException
     * @throws IOException 
     */
    public boolean connectToServer() throws IOException{       
        Verbindung verbindung;            
        ServerStub serverStub;
        Registry registry;

        boolean noConnection = true, result = false, vorhanden;
        String line, tmpIP;            
        BufferedReader bufferedReader = null;
        LinkedList<String> serverlist = new LinkedList<>();

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            while ((line = bufferedReader.readLine()) != null) { 
                serverlist.add(line);
            }             
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }          

        //Versucht Verbindung zu einem zufälligen Server aufzubauen
        while(noConnection && serverlist.size() > 0){
            tmpIP = serverlist.get((int) (Math.random() * serverlist.size()));
            System.out.println("LOG * ---> Versuche Verbindung zu " + tmpIP + " herzustellen!");  
            if(!this.ownIP.equals(tmpIP)){  
                vorhanden = false;
                for(Verbindung verb : this.connectionList){
                    if(verb.getIP().equals(tmpIP)){
                        vorhanden = true;
                    }
                }
                if(!vorhanden){
                    try {
                        //baut Verbindung zu Server auf
                        registry = LocateRegistry.getRegistry(tmpIP, 1100);
                        serverStub = (ServerStub) registry.lookup("ServerStub");

                        //lässt anderen Server Verbindung zu diesem aufbauen
                        serverStub.initConnection(this.ownIP);

                        //fügt Verbindung zur Liste der Verbindungen hinzu
                        verbindung = new Verbindung(serverStub, tmpIP);
                        this.connectionList.add(verbindung);

                        //Ausgabe im Terminal
                        System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " hergestellt!");

                        //Starte Threads, die die Verbindung zu anderen Servern testen
                        new VerbindungstestsThread(this, verbindung).start(); 
                        result = true;
                        noConnection = false;                    
                    } catch (RemoteException | NotBoundException ex) {
                        System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " konnte nicht hergestellt werden!");  
                    }    
                }
                else{
                    System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " schon vorhanden!");
                }                                    
            }
            else{
                System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " konnte nicht hergestellt werden! (eigener Server)");  
            }            
            serverlist.remove(tmpIP);
        } 
        
        return result;
    }   

 
}
