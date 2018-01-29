/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerStub;
import java.rmi.RemoteException;
import Server.ServerDaten;


public class PingThread extends Thread{
    
    // Variablen Deklaration und Initialisierung
    private final ServerStub serverStub;
    private final Counter counter;
    private final ServerDaten serverDaten;
    
    /**
     * Konstruktur
     * @param serverStub
     * @param counter
     * @param serverDaten 
     */
    public PingThread(ServerStub serverStub, Counter counter, ServerDaten serverDaten){
        this.serverStub = serverStub;
        this.counter = counter;
        this.serverDaten = serverDaten;
    }    
    
    /**
     * Methode die einen Server einen anderen Server anpingen laesst
     */
    @Override 
    public void run(){
        try {          
            if(this.serverStub.ping(serverDaten.primitiveDaten.ownIP)){
                //pingtest kam an, alles gut, resete counter
                counter.resetCounter();
            }
            else{
                //pingtest kam an, aber der andere Server 
                //hat keine Verbindung mehr zu diesem
                counter.setZero();
            }
        } catch (RemoteException ex) {
        }
    }
}
