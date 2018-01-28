package Server;

import Server.Utilities.Verbindung;
import ServerThreads.VerbindungstestsChildsThread;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Niroshan, Vincent
 */
public class ServerStubImpl implements ServerStub {

    private final ServerDaten serverDaten;

    ServerStubImpl(ServerDaten serverDaten) {
        this.serverDaten = serverDaten;
    }

    /**
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich
     * verbinden soll dient der Erzeugung einer beidseitigen Verbindung /
     * ungerichteten Verbindung
     *
     * @param childIP
     * @return childID Die neue ID wird zurückgegeben
     * @throws RemoteException
     * @throws AccessException
     */
    @Override
    public String initConnection(String childIP) throws RemoteException {
        try {
            String childID = this.serverDaten.primitiveDaten.getNewChildId();
            
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(childIP, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, childIP, childID);

            this.serverDaten.childConnection.add(verbindung);
            
            // Starte Thread, der die Verbindung zu anderen Servern testet
            new VerbindungstestsChildsThread(this.serverDaten, verbindung).start();

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu KindServer: ID  " + childID + " hergestellt!");
              
            return childID;
        } catch (NotBoundException | IOException e) {
            System.out.println("LOG * ---> Verbindung zu KindServer Fehler!");
            return null;
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
        boolean result = false;

        for (Verbindung childConnection : this.serverDaten.childConnection) {
            if (childConnection != null && childConnection.getIP().equals(senderIP)) {
                result = true;
            }
        }
        if (this.serverDaten.parent != null
                && this.serverDaten.parent.getIP().equals(senderIP)) {
            result = true;
        }

        return result;
    }
    
    /**
     * Gibt die ID des Servers zurueck
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
     public String getServerID() throws RemoteException{
        return this.serverDaten.primitiveDaten.serverID;         
    }
}
