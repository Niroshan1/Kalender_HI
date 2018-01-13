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
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich
     * verbinden soll dient der Erzeugung einer beidseitigen Verbindung /
     * ungerichteten Verbindung
     *
     * @param ip
     * @return
     * @throws RemoteException
     * @throws AccessException
     */
    @Override
    public boolean initConnection(String ip) throws RemoteException {
        try {
            boolean result = false;

            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, ip);

            for (int i = 1; i < this.serverDaten.childConnection.length; i++) {
                if (this.serverDaten.childConnection[i] == null) {
                    //speichert Verbinung als Kind
                    this.serverDaten.childConnection[i] = verbindung;

                    //Starte Threads, die die Verbindung zu anderen Servern testen
                    new VerbindungstestsThread(this.serverDaten, verbindung).start();
                    this.serverDaten.childCount[i] = String.valueOf(i) + "#";

                    //Ausgabe im Terminal            
                    System.out.println("LOG * ---> Verbindung zu KindServer: ID " + this.serverDaten.childCount[i] + " " + ip + " hergestellt!");

                    //Server hat verbindung
                    result = true;
                    

                    break;
                } else {
                    if (result == false) {
                        //Server hat problem => fehler
                        result = false;
                        System.out.println("LOG * ---> Verbindung zu KindServer FEHLER:");
                    }
                    return result;

                }
            }
        } catch (NotBoundException | IOException e) {
            return false;
        }
        return false;
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
        /**
         * if (this.serverDaten.leftchild != null &&
         * this.serverDaten.leftchild.getIP().equals(senderIP)) { result = true;
         *
         * } else if (this.serverDaten.rightchild != null &&
         * this.serverDaten.rightchild.getIP().equals(senderIP)) { result =
         * true;
         */

        for (int i = 1; i < this.serverDaten.childConnection.length; i++) {
            if (this.serverDaten.childConnection[i] != null
                    && this.serverDaten.childConnection[i].getIP().equals(senderIP)) {
                result = true;
            }
        }

        if (this.serverDaten.parent != null
                && this.serverDaten.parent.getIP()
                        .equals(senderIP)) {
            result = true;
        }

        return result;
    }

}
