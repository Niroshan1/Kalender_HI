package Client;


import Server.ClientStub;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;

/**
 * Startet den Client und verbindet zum root Server
 */
public class Client {
   
    /**
     * Main methode
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientStub stub;
        Registry registry;     
                
        String rootIP;
        String line;            
        BufferedReader bufferedReader = null;

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        //für mac-pcs
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            if((line = bufferedReader.readLine()) != null) { 
                rootIP = line;
                
                try {
                    //baut Verbindung zu Server auf
                    registry = LocateRegistry.getRegistry(rootIP, 1099);
                    stub = (ClientStub) registry.lookup("ClientStub");
                    System.out.println("LOG * ---> Verbindung zu Root-Server mit IP " + rootIP + " hergestellt!");

                    GUI gui = new GUI(stub);
                    gui.startGUI();

                    //TUI tui = new TUI(stub);
                    //tui.start();

                } catch (RemoteException | NotBoundException ex) {
                    System.out.println("LOG * ---> Verbindung zu Root-Server mit IP " + rootIP + " konnte nicht hergestellt werden!");  
                }
            }      
            else{
                System.out.println("LOG * ---> Verbindung zu Root-Server konnte nicht hergestellt werden!");
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
        // zum schließen des readers
        finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }          
    }  
    
}