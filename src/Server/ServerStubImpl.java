package Server;

import ServerThreads.VerbindungstestsThread;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.omg.CORBA.portable.RemarshalException;

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
     * @param ip
     * @return childID Die neue ID wird zurückgegeben
     * @throws RemoteException
     * @throws AccessException
     */
    @Override
    public String initConnection(String ip) throws RemoteException {
        try {

            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, ip);

            for (int i = 0; i < this.serverDaten.childConnection.length; i++) {
                if (this.serverDaten.childConnection[i] == null) {

                    // Speichert Verbindung als Kind
                    this.serverDaten.childConnection[i] = verbindung;

                    // Starte Thread, der die Verbindung zu anderen Servern testet
                    new VerbindungstestsThread(this.serverDaten, verbindung).start();

                    //Ausgabe im Terminal
                    System.out.println("LOG * ---> Verbindung zu KindServer: ID  " + this.serverDaten.serverID + String.valueOf(i) + " hergestellt!");

                    return this.serverDaten.serverID + String.valueOf(i);
                }
            }

            return null;

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
     *
     * Gibt Anzahl der Kalender
     *
     * @param serverID
     * @return
     * @throws RemoteException
     */
    @Override
    public int getkalenderAnzahl() throws RemoteException {
        return this.serverDaten.kalenderAnzahl;

    }
    
    @Override
    public void setKalenderAnzahl () throws RemoteException{
        this.serverDaten.kalenderAnzahl++;
    }
    
    /**
     * Gibt die ID des Servers zurueck
     * @return
     * @throws java.rmi.RemoteException
     * @throws RemarshalException 
     */
    @Override
     public String getServerID() throws  RemoteException, RemarshalException{
        return this.serverDaten.serverID;
         
     }
}
