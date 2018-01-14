
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
 * @author Niroshan, Vincent
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
            boolean result = true;
            
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, ip);
            
                for(int i = 0; i < this.serverDaten.childConnection.length; i++){
                    if (this.serverDaten.childConnection[i] == null) {
                        
                        // Speichert Verbindung als Kind
                        this.serverDaten.childConnection[i] = verbindung ;
                        
                        // Starte Thread, der die Verbindung zu anderen Servern testet
                        new VerbindungstestsThread(this.serverDaten, verbindung).start();
                        // TODO: Ergänzt die ID des Kindes
                        //this.serverDaten.childCount[i] = String.valueOf(i) + "#";
                        
                        //Ausgabe im Terminal
                         System.out.println("LOG * ---> Verbindung zu KindServer: ID  " + this.serverDaten.childConnection[i] + " " + ip +  " hergestellt!");
                         
                         result = true;
                         
                         break;
                    }else if(result == false) {
                        result = false; 
                        //Ausgabe im Terminal            
                        System.out.println("LOG * ---> Verbindung zu KindServer Fehler!"); 
                    }
                }
            
            /*
            //testet ob server noch kein linkes Kind hat
            if(this.serverDaten.leftchild == null){
                //speichert Verbinung als linkes Kind
                this.serverDaten.leftchild = verbindung;
                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this.serverDaten, verbindung).start();

                //Ausgabe im Terminal            
                System.out.println("LOG * ---> Verbindung zu linkem Kind " + ip + " hergestellt!"); 
            }
            //testet ob Server noch kein rechtes Kind had
            else if(this.serverDaten.rightchild == null){
                //speichert Verbindung als rechtes Kind
                this.serverDaten.rightchild = verbindung;
                //Starte Threads, die die Verbindung zu anderen Servern testen
                new VerbindungstestsThread(this.serverDaten, verbindung).start();

                //Ausgabe im Terminal            
                System.out.println("LOG * ---> Verbindung zu rechtem Kind " + ip + " hergestellt!"); 
            }           
            else{
                //Server hat schon 2 Kinder => fehler
                result = false;
            }
            */
            return result;
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
        boolean result = false;
        
        
        for (Verbindung childConnection : this.serverDaten.childConnection) {
            if (childConnection != null && childConnection.getIP().equals(senderIP)) {
                result = true;
            }
        }
        if(this.serverDaten.parent != null 
            && this.serverDaten.parent.getIP().equals(senderIP)){
            result = true;           
        }
        
        return result;
    }   
    
}
