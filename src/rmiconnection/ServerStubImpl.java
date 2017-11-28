
package rmiconnection;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nader
 */
public class ServerStubImpl implements ServerStub {
    
    private LinkedList<ServerStub> connectionList = new LinkedList<>();
        
    ServerStubImpl(LinkedList<ServerStub> connectionList) {
        this.connectionList = connectionList;
    }

    @Override
    public void reconnect(String ip, int port) throws RemoteException, AccessException {     
        Registry registry = LocateRegistry.getRegistry(ip, port);
        try {
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(stub);
            System.out.println("added server to List");
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
