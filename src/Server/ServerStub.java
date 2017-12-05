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
interface ServerStub extends Remote{
    
    public boolean initConnection(String ip, int port) throws RemoteException;
    
    public LinkedList<String> getOnlineServerList() throws RemoteException;   
    public void aktOnlineServerList(String ip) throws RemoteException;
    
    public boolean ping() throws RemoteException;   
    public boolean isServerReachable(String ip, int port) throws RemoteException;
    
    public void updateOnlineServerList(String ip) throws RemoteException;
}
