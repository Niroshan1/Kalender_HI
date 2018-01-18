/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Server.ClientStub;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;

/**
 *
 * @author Tim Meyer
 */
public class Client {
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* IP-Adresse des Servers */
        String ipaddr = "localhost";
        
        if ((args.length > 2)){
            System.err.println("java -jar Client_Terminkalender.jar <Server-IP-Adresse>");
        }
        else{
            /* falls IP-Adresse übergeben wurde, wird diese gesetzt */
            if(args.length == 1){
                ipaddr = args[0];
                System.out.println("IP-Adresse des Servers auf: " + ipaddr + " gesetzt!");
            }
            try {
                Registry registry = LocateRegistry.getRegistry(ipaddr);
                ClientStub stub = (ClientStub) registry.lookup("ClientStub");
                
                System.out.println("Mit Server verbunden!"); 
                
                GUI gui = new GUI(stub);
                gui.startGUI();
                
                TUI tui = new TUI(stub);
                tui.start();
            } 
            catch (NotBoundException | RemoteException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }
    
}
