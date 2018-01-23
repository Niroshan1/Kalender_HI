/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ClientStub;
import Server.ServerDaten;
import Utilities.DatenbankException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.portable.RemarshalException;


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
    public void run() {
        Counter counter = new Counter();
        boolean serverUp = true;

        while (serverUp) {
            try {
                //if(this.serverDaten.datenbank.getUserCounter() == 0)
                   // System.out.println("Keine Kalender!");
                //else
                    //System.out.println("Anzahl: " + this.serverDaten.datenbank.getUserCounter());
                // Anzahl mit wenige Kalender Server mit ID notieren
                //this.serverDaten.serverIDKind = this.serverDaten.serverKalenderAnzahlBewerten();

                // Server IP adresse mitspeichern
                //this.serverDaten.serverIPKind = this.serverDaten.findServer(this.serverDaten.serverIDKind);
               

                //System.out.println("Server hat " + this.serverDaten.serverKalenderAnzahlBewerten());
                //Thread.sleep(5000);
                System.out.println("OwnIP: " + this.serverDaten.ownIP);
                System.out.println("ParentIP: " + this.serverDaten.parentIP);
                System.out.println("ServerID: " + this.serverDaten.serverID);
                System.out.println("ServerIDKind: " + this.serverDaten.serverIDKind);
                System.out.println("ServerIPKind: " + this.serverDaten.serverIPKind);
//                System.out.println("Server hat " + this.serverDaten.serverKalenderAnzahlBewerten() + " Kalender");
                System.out.println("");
                System.out.println("");

                Thread.sleep(20000);

                //beende Schleife
                //serverUp = false;
            } catch (InterruptedException ex) {
                Logger.getLogger(KalenderAnzahlThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
