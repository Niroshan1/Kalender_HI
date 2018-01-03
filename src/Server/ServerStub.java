/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author nader
 */
public interface ServerStub extends Remote{
    
    public boolean initConnection(String ip) throws RemoteException;
    public boolean ping(String senderIP) throws RemoteException;   
}
