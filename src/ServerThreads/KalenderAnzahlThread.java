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
public class KalenderAnzahlThread extends Thread {

    ClientStub clientStub;
    ServerDaten serverDaten;
    ServerStub serverStub;

    /**
     *
     * @param clientStub
     * @param serverDaten
     * @param serverStub
     */
    public KalenderAnzahlThread(ClientStub clientStub, ServerDaten serverDaten, ServerStub serverStub) {
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
                System.out.println(serverID);
                if (serverID == null) {

                    serverID = this.serverDaten.serverKalenderAnzahlBewerten();
                    serverIP = this.serverDaten.findServer(serverID);

                    System.out.println(serverID);
                    System.out.println(serverIP);
                    
                    this.clientStub.setServerID(serverID);
                    this.clientStub.setServerIP(serverIP);

                    Thread.sleep(20000);
                } else if (serverIP.equals("Client hat bekommen")) {
                    serverID = this.clientStub.getServerID();
                    
                    for (int i = 0; i < this.serverDaten.childConnection.length; i++) {
                        if (serverID.equals(this.serverDaten.childConnection[i].getServerStub().getServerID())) {
                            this.serverDaten.childConnection[i].getServerStub().setKalenderAnzahl();
                            serverID = null;
                            serverIP = null;
                            this.clientStub.setServerID(serverID);
                            this.clientStub.setServerIP(serverIP);
                        }
                    }
                }

            } catch (InterruptedException | RemoteException | RemarshalException | SQLException | DatenbankException ex) {
                Logger.getLogger(KalenderAnzahlThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
