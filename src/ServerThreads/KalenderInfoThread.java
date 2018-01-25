/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ClientStub;
import Server.ServerDaten;
import Server.ServerStub;
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
public class KalenderInfoThread extends Thread {

    ClientStub clientStub;
    ServerDaten serverDaten;
    ServerStub serverStub;

    /**
     *
     * @param clientStub
     * @param serverDaten
     * @param serverStub
     */
    public KalenderInfoThread(ClientStub clientStub, ServerDaten serverDaten, ServerStub serverStub) {
        this.clientStub = clientStub;
        this.serverDaten = serverDaten;
        this.serverStub = serverStub;
    }

    @Override
    public void run() {

        boolean serverUp = true;
        
        String serverID = null;
        String serverIP = null;
        

        while (serverUp) {
            try {
                Thread.sleep(5000);

                serverID = this.clientStub.getServerID();
                serverIP = this.clientStub.getServerIP();
                System.out.println("momentan ID " + serverID);
                System.out.println("momentan IP " + serverIP);
                if (serverID == null) {

                    serverID = this.serverDaten.serverKalenderAnzahlBewerten();
                    serverIP = this.serverDaten.findServer(serverID);

                    System.out.println("ServerID mit weniger Kalender: " + serverID);
                    System.out.println("ServerIP mit weniger Kalender: " + serverIP);

                    this.clientStub.setServerID(serverID);
                    this.clientStub.setServerIP(serverIP);

                    //Thread.sleep(5000);
                }

                if (serverIP.equals("Client hat bekommen")) {
                    serverID = this.clientStub.getServerID();
                    System.out.println("Client hat sich gemeldet " + serverID);
                    System.out.println("ServerID: " + serverIP);

                    if (serverID.equals(this.serverDaten.serverID)) {
                        this.serverDaten.kalenderAnzahl++;
                        serverID = null;
                        serverIP = null;
                        System.out.println("IP zurueckgesetzt " + serverIP);
                        System.out.println("ID zurueckgesetzt: " + serverID);
                        this.clientStub.setServerID(null);
                        this.clientStub.setServerIP(null);
                        
                    } else {
                        for (int i = 0; i < this.serverDaten.childConnection.length; i++) {

                            if (this.serverDaten.childConnection[i] != null) {
                                if (serverID.equals(this.serverDaten.childConnection[i].getServerStub().getServerID())) {
                                    this.serverDaten.childConnection[i].getServerStub().setKalenderAnzahlHoch();
                                    serverID = null;
                                    serverIP = null;
                                    System.out.println("IP zurueckgesetzt " + serverIP);
                                    System.out.println("ID zurueckgesetzt: " + serverID);
                                    this.clientStub.setServerID(null);
                                    this.clientStub.setServerIP(null);
                                }
                            }
                        }
                    }
                }

            } catch (InterruptedException | RemoteException | RemarshalException | SQLException | DatenbankException ex) {
                Logger.getLogger(KalenderInfoThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
