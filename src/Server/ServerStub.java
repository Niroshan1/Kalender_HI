/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author nader
 */
public interface ServerStub extends Remote{
    
    public boolean initConnection(String ip) throws RemoteException;
    
    public LinkedList<String> getOnlineServerList() throws RemoteException;   
    
    public boolean ping() throws RemoteException;   
    public boolean isServerReachable(String ip) throws RemoteException;
    
    public void updateOnlineServerList(String neueIP, String senderIP) throws RemoteException;
    public void entferneServerAusSystem(String serverIP, String senderIP) throws RemoteException;
}
