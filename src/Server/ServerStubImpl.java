
package Server;

import ServerThreads.VerbindungstestsThread;
import Utilities.DBHandler;
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
            System.out.println("onlineserverliste:");
            for(String bla : this.serverDaten.onlineServerList){
                System.out.println(bla);
            }
            
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, ip);
            this.serverDaten.connectionList.add(verbindung);
            new VerbindungstestsThread(this.serverDaten, verbindung).start();
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
        return this.serverDaten.onlineServerList;
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
        for(Verbindung connection : this.serverDaten.connectionList){
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
        if(!this.serverDaten.onlineServerList.contains(neueIP)){
            this.serverDaten.onlineServerList.add(neueIP);
            for(Verbindung verbindung : this.serverDaten.connectionList){
                if(!verbindung.getIP().equals(senderIP)){
                    new Thread(() -> {
                        try {
                            verbindung.getServerStub().updateOnlineServerList(neueIP, this.serverDaten.ownIP);
                        }catch (RemoteException ex) {
                            Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }).start();
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
        System.out.println("---------------------------------------------------------------------------");
        if(!this.serverDaten.onlineServerList.contains(serverIP)){
            if(this.serverDaten.ownIP.equals(serverIP)){
                //TODO: server neu in system einbinden
                System.out.println("ich bins!");
            }           
            else{
                System.out.println("onlineserverliste:");
                for(String bla : this.serverDaten.onlineServerList){
                    System.out.println(bla);
                }
                
                //server aus liste der online server entfernen
                this.serverDaten.onlineServerList.remove(serverIP);               
                
                //info via flooding weiterleiten
                for(Verbindung verbindung : this.serverDaten.connectionList){
                    //hier threads + flooding
                    new Thread(() -> {
                        try {
                            verbindung.getServerStub().entferneServerAusSystem(serverIP, ServerStubImpl.this.serverDaten.ownIP);
                        }catch (RemoteException ex) {
                            Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }).start();
                    
                    if(verbindung.getIP().equals(serverIP)){
                        //server aus connectionlist entfernen
                        this.serverDaten.connectionList.remove(verbindung);
                    }            
                }
            }
        }
    }
    
}
