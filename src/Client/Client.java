/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Server.ClientStub;
import Server.Server;
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
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Client start = new Client();
        ClientStub stub = null;
        /* IP-Adresse des Servers */
        String ipaddr = "localhost";
        

        if ((args.length > 2)) {
            System.err.println("java -jar Client_Terminkalender.jar <Server-IP-Adresse>");
        } else {
            /* falls IP-Adresse übergeben wurde, wird diese gesetzt */
            if (args.length == 1) {
                ipaddr = args[0];
                System.out.println("IP-Adresse des Servers auf: " + ipaddr + " gesetzt!");
            }
            stub = start.askRootServer(ipaddr);
            
            start.clientStart(stub);

        }
        
        
    }

    public ClientStub askRootServer (String ipaddr) throws RemoteException, NotBoundException {
        try {
            ClientStub stub = null;
            Registry registry = LocateRegistry.getRegistry(ipaddr);
            stub = (ClientStub) registry.lookup("0");
            System.out.println("Mit Root Server verbunden!");
            
            return stub;
                

        } catch (NotBoundException | RemoteException e) {
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Exception: Kein Root Server");
        }
        return null;
    }
    
    public void clientStart(ClientStub stubAkt) {
        try {
            
            String ipaddr = stubAkt.getServerIP();
            String serverID = stubAkt.getServerID();
            
            System.out.println(ipaddr + " Client");
            System.out.println(serverID + " Client");
            
            Registry registry = LocateRegistry.getRegistry(ipaddr);
            ClientStub stub = (ClientStub) registry.lookup(serverID);

            System.out.println("Mit Server verbunden!");

            stubAkt.setServerIP("Client hat bekommen");
            stubAkt = null;
            
            System.out.println("Client hat gesetet");
            
            GUI gui = new GUI(stub);
            gui.startGUI();

            TUI tui = new TUI(stub);
            tui.start();
        } catch (NotBoundException | RemoteException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

}
