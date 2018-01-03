/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DatenbankException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
     * baut bis zu 2 Verbindungen zu anderen Servern auf
     * 
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IOException 
     */
    private void connectToServers() throws IOException{
        try {
            //TODO: lesen aus onlinefile!!
            
            
            BufferedReader bufferedReader;
            String line;
           // File file = new File("https://1drv.ms/t/s!AjRYgaF5cS41q1BbhwaaWJip_jHP");
            URL url = new URL("https://1drv.ms/t/s!AjRYgaF5cS41q1Fuz38Cr_X-rBka");
            //Scanner s = new Scanner(url.openStream());
                       
            int counter = 0;
            boolean check = true;
            OutputStreamWriter fileOut;
            String[] words , besteZeile1 = null , besteZeile2 = null;
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer inputBuffer = new StringBuffer();
            
            //sucht 2 Server mit den Wenigsten Verbindungen zum Verbinden
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    words = line.split(" ");
                    if((besteZeile1 == null) || (Integer.parseInt(words[1]) < Integer.parseInt(besteZeile1[1]))){
                        besteZeile2 = besteZeile1;
                        besteZeile1 = words;
                    }
                    else if((besteZeile2 == null) || (Integer.parseInt(words[1]) < Integer.parseInt(besteZeile2[1])) ){
                        besteZeile2= words;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            bufferedReader.close();          
            
            //Verbindung zu besteEins aufbauen falls nicht null
            if(connectTo(besteZeile1)){
                counter++;
            }        
                     
            //Verbindung zu besteZwei aufbauen falls nicht null
            if(connectTo(besteZeile2)){
                counter++;
            }
            
            //aktuallisiert eigene eintraege in der serverliste
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while((line = bufferedReader.readLine()) != null){
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

    /**
     * baut eine Verbindung zu einem Server auf, lässt dann den server mit diesem 
     * verbinden und aktuallisiert am Ende die serverlist
     * 
     * @param serverlistElement
     * @return 
     */
    private boolean connectTo(String[] serverlistElement) throws IOException{
        Verbindung verbindung;            
        ServerStub serverStub;
        Registry registry;
        
        if(serverlistElement != null){
            try {
                //baut Verbindung zu Server auf
                registry = LocateRegistry.getRegistry(serverlistElement[0], 1100);
                serverStub = (ServerStub) registry.lookup("ServerStub");
                
                //lässt anderen Server Verbindung zu diesem aufbauen
                serverStub.initConnection(this.serverDaten.ownIP);

                //fügt Verbindung zur Liste der Verbindungen hinzu
                verbindung = new Verbindung(serverStub, serverlistElement[0]);
                this.serverDaten.connectionList.add(verbindung);
                
                //Ausgabe im Terminal
                System.out.println("LOG * ---> Verbindung zu Server " + serverlistElement[0] + " hergestellt!");

                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this.serverDaten, verbindung).start();   
                
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        return false;
    }
    
    
}
