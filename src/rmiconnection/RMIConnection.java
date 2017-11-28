/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiconnection;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author nader
 */
public class RMIConnection {

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     */
 public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException{
     
    
        LinkedList<ServerStub> connectionList = new LinkedList<>();
        
       
     
        ServerStubImpl serverLauncher = new ServerStubImpl(connectionList);
        
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverRegistry );
            
        System.out.println("ServerStub angelegt!");
        
        
        ClientStubImpl clientLauncher = new ClientStubImpl();
        
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
            
        System.out.println("ClientStub angelegt!");
        
        if(args.length > 0){
            String ip= args[0];
            int port = Integer.parseInt(args[1]);


            Registry registry = LocateRegistry.getRegistry(ip, port);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(stub); 

        }
        System.out.println("Server laeuft!");
        
    }      
    
}
