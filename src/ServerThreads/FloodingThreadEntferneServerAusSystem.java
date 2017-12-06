/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerStub;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class FloodingThreadEntferneServerAusSystem extends Thread{
    
    private final ServerStub verbindung;
    private final String serverIP;
    private final String ownIP;
    
    public FloodingThreadEntferneServerAusSystem(ServerStub verbindung, String serverIP, String ownIP){
        this.verbindung = verbindung;
        this.serverIP = serverIP;
        this.ownIP = ownIP;
    }    
    
    @Override 
    public void run(){
        try {        
            verbindung.entferneServerAusSystem(serverIP, ownIP);
        } catch (RemoteException ex) {
            Logger.getLogger(FloodingThreadAktOnlineServerList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
