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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.AccessException;
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
import java.util.Scanner;
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
              
        //initialisiere Stubs für Server & Clients
        System.out.println("LOG * ");
        initServerStub();
        initClientStub();
        //baue bis zu 2 dauerhafte Verbindungen zu anderen Servern auf
        connectToServers();

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
   
   
    /**
     * baut bis zu 2 Verbindungen zu anderen Servern auf
     * 
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IOException 
     */
    private void connectToServers() throws IOException{
        
        try {
            BufferedReader bufferedReader;
            String line;
           // File file = new File("https://1drv.ms/t/s!AjRYgaF5cS41q1BbhwaaWJip_jHP");
            Verbindung verbindung;
            URL url = new URL("https://1drv.ms/t/s!AjRYgaF5cS41q1Fuz38Cr_X-rBka");
            //Scanner s = new Scanner(url.openStream());
            
            
         
            ServerStub stub = null, stubTmp = null;
            Registry registry;
            int counter = 0;
            boolean check = true;
            OutputStreamWriter fileOut;
            String[] words , besteEins = null , besteZwei = null;
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer inputBuffer = new StringBuffer();
            
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    words = line.split(" ");
                    if((besteEins == null) || (Integer.parseInt(words[1]) < Integer.parseInt(besteEins[1]))){
                        besteZwei = besteEins;
                        besteEins = words;
                    }
                    else if((besteZwei == null) || (Integer.parseInt(words[1]) < Integer.parseInt(besteZwei[1])) ){
                        besteZwei= words;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            bufferedReader.close();
            
            
            
            //Verbindung zu besteEins aufbauen falls nicht null
            if( besteEins != null){
                try {
                    registry = LocateRegistry.getRegistry(besteEins[0], 1100);
                    stubTmp = (ServerStub) registry.lookup("ServerStub");
                    //lässt anderen Server Verbindung zu diesem aufbauen
                    stubTmp.initConnection(this.serverDaten.ownIP);
                     //fügt Verbindung zur Liste der Verbindungen hinzu
                    verbindung = new Verbindung(stub, besteEins[0]);
                    this.serverDaten.connectionList.add(verbindung);
                    System.out.println("LOG * ---> Verbindung zu Server " + besteEins[0] + " hergestellt!");
                    //Starte Threads, die die Verbindung zu anderen Servern testen
                    new VerbindungstestsThread(this.serverDaten, verbindung).start();
                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    while ( (line = bufferedReader.readLine()) != null){
                        words = line.split(" ");
                        if(words[0].equals(besteEins[0])){
                            
                            line = words[0] + " " + words[1] + " 0";
                        }
                        inputBuffer.append(line);
                        inputBuffer.append('\n');    
                    }
                    
                    bufferedReader.close();
                    fileOut = new OutputStreamWriter(url.openConnection().getOutputStream());
                    fileOut.write(inputBuffer.toString());
                    fileOut.close();
                    counter++;
                } catch (RemoteException | NotBoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
            
            
            
            //Verbindung zu besteZwei aufbauen falls nicht null
            if( besteZwei != null){
                try {
                    registry = LocateRegistry.getRegistry(besteZwei[0], 1100);
                     stubTmp = (ServerStub) registry.lookup("ServerStub");
                    //lässt anderen Server Verbindung zu diesem aufbauen
                    stubTmp.initConnection(this.serverDaten.ownIP);

                    //fügt Verbindung zur Liste der Verbindungen hinzu
                    verbindung = new Verbindung(stub, besteZwei[0]);
                    this.serverDaten.connectionList.add(verbindung);
                    System.out.println("LOG * ---> Verbindung zu Server " + besteZwei[0] + " hergestellt!");

                    //Starte Threads, die die Verbindung zu anderen Servern testen
                    new VerbindungstestsThread(this.serverDaten, verbindung).start();
                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    while ( (line = bufferedReader.readLine()) != null){
                        words = line.split(" ");
                        if(words[0].equals(besteZwei[0])){
                            
                            line = words[0] + " " + words[1] + " 0";
                        }
                        inputBuffer.append(line);
                        inputBuffer.append('\n');    
                    }
                    
                    bufferedReader.close();
                    fileOut = new OutputStreamWriter(url.openConnection().getOutputStream());
                    fileOut.write(inputBuffer.toString());
                    fileOut.close();
                    counter++;
                } catch (RemoteException | NotBoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ( (line = bufferedReader.readLine()) != null){
                words = line.split(" ");
                if(words[0].equals(serverDaten.ownIP)){
                    check = false;      
                    line = words[0] + " " + counter + " 0";
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');    
            }
            if(check){
                inputBuffer.append(serverDaten.ownIP + " " + counter + " 0");
                inputBuffer.append('\n');
                
            }
            bufferedReader.close();
            fileOut = new OutputStreamWriter(url.openConnection().getOutputStream());
            fileOut.write(inputBuffer.toString());
            fileOut.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
                
                
    }

}
