/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ClientStub;
import Server.ServerStub;
import java.rmi.RemoteException;
import Server.ServerDaten;
import Utilities.DatenbankException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.portable.RemarshalException;

/**
 *
 * @author timtim
 */
public class KalenderAnzahlThread extends Thread{
    
    //private final ServerStub serverStub;
    private final ClientStub clientStub;
    //private final Counter counter;
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
