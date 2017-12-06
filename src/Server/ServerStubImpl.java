
package Server;

import ServerThreads.FloodingThreadAktOnlineServerList;
import ServerThreads.FloodingThreadEntferneServerAusSystem;
import Utilities.DBHandler;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;

/**
 *
 * @author nader
 */
public class ServerStubImpl implements ServerStub {
    
    private final String ownIP;
    private final LinkedList<Verbindung> connectionList;
    private final LinkedList<String> onlineServerList;
    private final DBHandler datenbank;
        
    ServerStubImpl(LinkedList<Verbindung> connectionList, LinkedList<String> onlineServerList, DBHandler datenbank, String ownIP) {
        this.connectionList = connectionList;
        this.onlineServerList = onlineServerList;
        this.datenbank = datenbank;
        this.ownIP = ownIP;
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
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            connectionList.add(new Verbindung(stub, ip));
            System.out.println("Dauerhafte Verbindung zu Server " + ip + " hergestellt!");            
            return true;
        } catch (NotBoundException e) {
            return false;
        }
    }

    /**
     * gibt eine Liste aller Server zurueck, die gerade online und im Verbund sind
     * 
     * @return
     * @throws RemoteException 
     */
    @Override
    public LinkedList<String> getOnlineServerList() throws RemoteException {
        return onlineServerList;
    }

    /**
     * Methode um zu testen, ob noch eine Verbindung zum Server besteht
     * 
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    /**
     * Methode die testet, ob ein bestimmter Server noch erreichbar ist
     * 
     * @param ip
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean isServerReachable(String ip) throws RemoteException {
        for(Verbindung connection : connectionList){
            if(connection.equals(ip)){
                return connection.getServerStub().ping();
            }
        }
        // hier fehlt noch, dass die nachbarn nun das selbe tun sollen
        return false;
    }

    /**
     * checkt ob ip schon in onlineServerList vorhanden
     * wenn nein, wird sie eingefügt und alle Nachbarn werden benachrichtig 
     * (mit Threads -> parallel)
     * 
     * @param neueIP
     * @param senderIP
     * @throws RemoteException 
     */
    @Override
    public void updateOnlineServerList(String neueIP, String senderIP) throws RemoteException {
        if(!this.onlineServerList.contains(neueIP)){
            this.onlineServerList.add(neueIP);
            for(Verbindung verbindung : this.connectionList){
                if(!verbindung.getIP().equals(senderIP)){
                    new FloodingThreadAktOnlineServerList(verbindung.getServerStub(), neueIP, this.ownIP).start();
                }    
            }
        }
    }

    /**
     * entfernt serverIP aus der onlineServerList, falls vorhanden & 
     * sendet lösch-info via flooding & threads weiter
     * löscht außerdem serverIP aus connectionlist, falls vorhanden 
     * 
     * @param serverIP
     * @param senderIP
     * @throws RemoteException 
     */
    @Override
    public void entferneServerAusSystem(String serverIP, String senderIP) throws RemoteException {
        if(!this.onlineServerList.contains(serverIP)){
            if(this.ownIP.equals(serverIP)){
                //server neu in system einbinden
            }
            else{
                //server aus liste der online server entfernen
                this.onlineServerList.remove(serverIP);               
                
                //info via flooding weiterleiten
                for(Verbindung verbindung : this.connectionList){
                    //hier threads + flooding
                    new FloodingThreadEntferneServerAusSystem(verbindung.getServerStub(), serverIP, this.ownIP).start();
                    if(verbindung.getIP().equals(serverIP)){
                        //server aus connectionlist entfernen
                        connectionList.remove(verbindung);
                    }            
                }
            }
        }
    }
    
}
