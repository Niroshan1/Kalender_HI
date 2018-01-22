/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ClientStub;
import Server.ServerDaten;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author timtim
 */
public class KalenderAnzahlThread extends Thread{
    
    private final ClientStub clientStub;
    private final ServerDaten serverDaten;
    
    public KalenderAnzahlThread(ClientStub clientStub, ServerDaten serverDaten){
        //this.serverStub = serverStub;
        this.clientStub = clientStub;
        this.serverDaten = serverDaten;
    }    
    
    @Override 
    public void run(){
        try { 
            clientStub.setServerID("1");
            clientStub.setServerIP("1"); 
        } catch (RemoteException ex) {
            Logger.getLogger(KalenderAnzahlThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
