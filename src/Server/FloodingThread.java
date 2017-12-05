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
public class FloodingThread extends Thread{
    
    private final ServerStub verbindung;
    private final String ip;
    
    public FloodingThread(ServerStub verbindung, String ip){
        this.verbindung = verbindung;
        this.ip = ip;
    }    
    
    @Override 
    public void run(){
        try {        
            verbindung.aktOnlineServerList(ip);
        } catch (RemoteException ex) {
            Logger.getLogger(FloodingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
