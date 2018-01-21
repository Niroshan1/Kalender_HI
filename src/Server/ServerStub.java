/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.RemarshalException;

/**
 *
 * @author nader
 */
public interface ServerStub extends Remote{
    
    public String initConnection(String ip) throws RemoteException;
    public boolean ping(String senderIP) throws RemoteException;
    public int kalenderAnzahl () throws RemoteException;
    public String getServerID() throws RemoteException, RemarshalException; 
    
    
    // public .... ladeDB(...) throws RemoteException;
}
