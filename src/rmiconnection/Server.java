/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiconnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author timtim
 */
public class Server {

    private LinkedList<ServerStub> connectionList;
    
    public Server(){
        this.connectionList = new LinkedList<>();
    }

    void start(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
  
        initServerStub();
        initClientStub();
       
        
        if(args.length > 0){
            String ip= args[0];
            int port = Integer.parseInt(args[1]);


            Registry registry = LocateRegistry.getRegistry(ip, port);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(stub); 
            stub.reconnect(ip, port);

        }
        
        InetAddress ipAddr = InetAddress.getLocalHost();
        System.out.println(ipAddr.getHostAddress());
        
        System.out.println("Server laeuft!");
    }

    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(connectionList);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub );
    }

    private void initClientStub() throws RemoteException, AlreadyBoundException{
        ClientStubImpl clientLauncher = new ClientStubImpl();   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
    }
    
}
