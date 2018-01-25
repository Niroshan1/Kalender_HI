/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerThreads.KalenderInfoThread;
import Utilities.DatenbankException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import org.omg.CORBA.portable.RemarshalException;

/**
 *
 * @author timtim
 */
public class Server {

    private final ServerDaten serverDaten;
    private final String[] args;

    public Server(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        this.serverDaten = new ServerDaten(args);
        this.args = args;
        System.setProperty("java.rmi.server.hostname", args[0]);
    }

    /**
     * Startet den Server und ruft alle Methoden auf, die dazu notwendig sind
     *
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws SQLException
     * @throws DatenbankException
     * @throws IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.security.NoSuchAlgorithmException
     * @throws org.omg.CORBA.portable.RemarshalException
     */
    public void start() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException, ClassNotFoundException, NoSuchAlgorithmException, RemarshalException {

        System.out.println("LOG * Starte Server");
        System.out.println("LOG * Server-IP: " + serverDaten.ownIP);
        System.out.println("LOG * ");

        //initialisiere Stubs für Server & Clients
        System.out.println("LOG * ");
        ServerStub serverStub = initServerStub();
        

        //baut Verbindung zu Parent auf
        if (!args[1].equals("root")) {
            
            //this.serverDaten.serverDatenSpeicherung();
            this.serverDaten.connectToParent();
            this.serverDaten.ladeDatenbank();
            //initialisiere Stubs für Clients
            System.out.println("LOG * ");
            ClientStub clientStub = initClientStub();

        } else {
            this.serverDaten.ladeDatenbank();
            
            //initialisiere Stubs für Clients
            System.out.println("LOG * ");
            ClientStub clientStub = initClientStub();
            
            //initialisiere Stubs für Clients
            System.out.println("LOG * ");
            //Nach initialisierung aktualisiert Root ueber thread
            new KalenderInfoThread(clientStub, this.serverDaten, serverStub).start();
            
            
            System.out.println("LOG * ");
            System.out.println("LOG * " + args[1] + " Server laeuft!");

        }

        System.out.println("LOG * ");
        System.out.println("LOG * Server laeuft!");
        System.out.println("---------------------------------------------");
    }

    /**
     * initialisiert den Stub für die Server
     *
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    private ServerStub initServerStub() throws RemoteException, AlreadyBoundException {
        ServerStubImpl serverLauncher = new ServerStubImpl(serverDaten);
        ServerStub serverStub = (ServerStub) UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub);
        System.out.println("LOG * ServerStub initialisiert!");
        return serverStub;
    }

    /**
     * initialisiert den Stub für die Clients
     *
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws SQLException
     * @throws DatenbankException
     */
    private ClientStub initClientStub() throws RemoteException, AlreadyBoundException, SQLException, DatenbankException {
        ClientStubImpl clientLauncher = new ClientStubImpl(this.serverDaten.datenbank);
        ClientStub clientStub = (ClientStub) UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind(this.serverDaten.serverID, clientStub);
        System.out.println("LOG * ClientStub initialisiert!");
        
        return clientStub;
    }
    
    /**
     * initialisiert den Stub für die Clients
     *
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws SQLException
     * @throws DatenbankException
     */
    private ClientStub initTMPClientStub() throws RemoteException, AlreadyBoundException, SQLException, DatenbankException {
        ClientStubImpl clientLauncherTMP = new ClientStubImpl(this.serverDaten.tmpDatenbank);
        ClientStub clientStubTMP = (ClientStub) UnicastRemoteObject.exportObject(clientLauncherTMP, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStubTMP", clientStubTMP);
        System.out.println("LOG * ClientStub fuer neuer Client initialisiert!");

        return clientStubTMP;
    }
    
}
