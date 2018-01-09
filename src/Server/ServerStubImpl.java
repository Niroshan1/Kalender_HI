
package Server;

import ServerThreads.VerbindungstestsThread;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author nader
 */
public class ServerStubImpl implements ServerStub {
    
    private final ServerDaten serverDaten;
          
    ServerStubImpl(ServerDaten serverDaten) {
        this.serverDaten = serverDaten;
    }

    /**
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich verbinden soll
     * dient der Erzeugung einer beidseitigen Verbindung / ungerichteten Verbindung
     * 
     * @param ip
     * @return 
     * @throws RemoteException
     * @throws AccessException 
     */
    @Override
    public boolean initConnection(String ip) throws RemoteException{           
        try {    
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            
            //fügt Verbindung zur Liste der Verbindungen hinzu
            Verbindung verbindung = new Verbindung(stub, ip);
            this.serverDaten.connectionList.add(verbindung);
            
            //Starte Threads, die die Verbindung zu anderen Servern testen
            new VerbindungstestsThread(this.serverDaten, verbindung).start();
            
            //Ausgabe im Terminal            
            System.out.println("Dauerhafte Verbindung zu Server " + ip + " hergestellt!");       
            
            return true;
        } catch (NotBoundException | IOException e) {
            return false;
        }
    }


    /**
     * Methode um zu testen, ob noch eine Verbindung zum Server besteht
     * 
     * @param senderIP
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean ping(String senderIP) throws RemoteException {
        for(Verbindung verbindung : serverDaten.connectionList){
            if(verbindung.getIP().equals(senderIP)){
                return true;
            }
        }
        return false;
    }   

    /**
     * sucht den server mit der db eines bestimmten users und gibt ip des servers zurueck
     * 
     * @param username
     * @return
     * @throws RemoteException 
     */
    @Override
    public String findServerForUser(String username, String originIP, String senderIP) throws RemoteException {
        //suche in db nach username
        //falls gefunden: return ownIP
        //sonst flooding: rufe diese methode bei allen der connectionList auf
        return "";
    }
    
}
