/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ClientStub;
import Server.ServerDaten;


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
        clientStub.setServerID(this.serverDaten.serverIDvonKind); 
        clientStub.setServerIP(this.serverDaten.serverIPvonKind); 
    }
}
