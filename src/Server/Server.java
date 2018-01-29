/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DatenbankException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Diese Klasse enthält Methoden zum initialisieren Client und Server Stub, sowie zum starten
 * 
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
     * @throws UnknownHostException
     * @throws SQLException
     * @throws Server.Utilities.DatenbankException
     * @throws IOException
     */
    public void start() throws RemoteException, AlreadyBoundException, SQLException, IOException, DatenbankException {
        
        System.out.println("LOG * Starte Server");
        System.out.println("LOG * Server-IP: " + serverDaten.primitiveDaten.ownIP);
        System.out.println("LOG * ");

        //initialisiere Stubs für Server & Clients      
        initServerStub();
        initClientStub();
        System.out.println("LOG * ");
        
        //baut Verbindung zu Parent auf
        if (!args[1].equals("root")) {          
            this.serverDaten.connectToParent(args[1]);
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
        ClientStubImpl clientLauncher = new ClientStubImpl(serverDaten);
        ClientStub clientStub = (ClientStub) UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
        System.out.println("LOG * ClientStub initialisiert!");
        
        return clientStub;
    }
    
}
