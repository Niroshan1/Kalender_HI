/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

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
public class PingThread extends Thread{
    
    private final ServerStub serverStub;
    private final Counter counter;
    private final ServerDaten serverDaten;
    
    public PingThread(ServerStub serverStub, Counter counter, ServerDaten serverDaten){
        this.serverStub = serverStub;
        this.counter = counter;
        this.serverDaten = serverDaten;
    }    
    
    @Override 
    public void run(){
        try {          
            if(this.serverStub.ping(serverDaten.ownIP)){
                //pingtest kam an, alles gut, resete counter
                counter.resetCounter();
                
                if(this.serverDaten.ownIP.equals(this.serverDaten.parentIP)) {
                    // Anzahl mit wenige Kalender Server mit ID notieren
                    this.serverDaten.serverIDKind = this.serverDaten.serverKalenderAnzahlBewerten();
                    
                    // Server IP adresse mitspeichern
                    this.serverDaten.serverIPKind = this.serverDaten.findServer(this.serverDaten.serverIDKind);
                    
                }
            }
            else{
                //pingtest kam an, aber der andere Server 
                //hat keine Verbindung mehr zu diesem
                counter.setNegativ();
            }
        } catch (RemoteException ex) {
        } catch (RemarshalException | SQLException | DatenbankException ex) {
            Logger.getLogger(PingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
