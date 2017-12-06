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
    private final String neueIP;
    private final String ownIP;
    
    public FloodingThreadEntferneServerAusSystem(ServerStub verbindung, String neueIP, String ownIP){
        this.verbindung = verbindung;
        this.neueIP = neueIP;
        this.ownIP = ownIP;
    }    
    
    @Override 
    public void run(){
        try {        
            verbindung.entferneServerAusSystem(neueIP, ownIP);
        } catch (RemoteException ex) {
            Logger.getLogger(FloodingThreadAktOnlineServerList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
