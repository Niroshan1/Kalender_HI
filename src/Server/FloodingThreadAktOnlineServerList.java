/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class FloodingThreadAktOnlineServerList extends Thread{
    
    private final ServerStub verbindung;
    private final String neueIP;
    private final String ownIP;
    
    public FloodingThreadAktOnlineServerList(ServerStub verbindung, String neueIP, String ownIP){
        this.verbindung = verbindung;
        this.neueIP = neueIP;
        this.ownIP = ownIP;
    }    
    
    @Override 
    public void run(){
        try {        
            verbindung.updateOnlineServerList(neueIP, ownIP);
        } catch (RemoteException ex) {
            Logger.getLogger(FloodingThreadAktOnlineServerList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
