/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
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
     * @throws RemoteException
     * @throws IOException 
     */
    public void connectToServer() throws IOException{
        try {
            //TODO: lesen aus onlinefile!!
            Verbindung verbindung;            
            ServerStub serverStub;
            Registry registry;
            
            BufferedReader bufferedReader;
            String line;
           // File file = new File("https://1drv.ms/t/s!AjRYgaF5cS41q1BbhwaaWJip_jHP");
            URL url = new URL("https://1drv.ms/t/s!AjRYgaF5cS41q1Fuz38Cr_X-rBka");
            //Scanner s = new Scanner(url.openStream());
                       
            boolean check = true;
            OutputStreamWriter fileOut;
            String[] words , besteZeile = null;
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer inputBuffer = new StringBuffer();
            boolean contain;
            
            //sucht Server mit den wenigsten Verbindungen, mit dem er noch 
            //nicht verbunden ist, zum Verbinden
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    words = line.split(" ");
                    if((besteZeile == null) || (Integer.parseInt(words[1]) < Integer.parseInt(besteZeile[1]))){
                        contain = false;
                        for(Verbindung verb : this.connectionList){
                            if(verb.getIP().equals(words[0])){
                                contain = true;
                            }                            
                        }
                        if(!contain){
                            besteZeile = words;
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            bufferedReader.close();          
            
            //baut Verbindung zu Server auf
            if(besteZeile != null){
                try {
                    //baut Verbindung zu Server auf
                    registry = LocateRegistry.getRegistry(besteZeile[0], 1100);
                    serverStub = (ServerStub) registry.lookup("ServerStub");

                    //lässt anderen Server Verbindung zu diesem aufbauen
                    serverStub.initConnection(this.ownIP);

                    //fügt Verbindung zur Liste der Verbindungen hinzu
                    verbindung = new Verbindung(serverStub, besteZeile[0]);
                    this.connectionList.add(verbindung);

                    //Ausgabe im Terminal
                    System.out.println("LOG * ---> Verbindung zu Server " + besteZeile[0] + " hergestellt!");

                    //Starte Threads, die die Verbindung zu anderen Servern testen
                    new VerbindungstestsThread(this, verbindung).start();   

                } catch (RemoteException | NotBoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            else{
                System.out.println("LOG * ---> kein weiterer Server zum Verbinden vorhanden");
            }
            
            //aktuallisiert eigene eintraege in der serverliste
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while((line = bufferedReader.readLine()) != null){
                words = line.split(" ");
                if(words[0].equals(this.ownIP)){
                    check = false;      
                    line = words[0] + " " + this.connectionList.size() + " 0";
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');    
            }
            if(check){
                inputBuffer.append(this.ownIP + " " + this.connectionList.size() + " 0");
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
