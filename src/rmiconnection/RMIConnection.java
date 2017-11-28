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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nader
 */
public class RMIConnection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            
            Server server = new Server();
            server.start(args);
            
        } catch (RemoteException | AlreadyBoundException | NotBoundException | UnknownHostException ex) {
            Logger.getLogger(RMIConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }      
    
}
