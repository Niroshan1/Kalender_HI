
package Server;

import ServerThreads.VerbindungstestsThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author nader
 */
public class ServerStubImpl implements ServerStub {
    
    private final ServerDaten serverDaten;
          
    ServerStubImpl(ServerDaten serverDaten) {
        this.serverDaten = serverDaten;
    }

    /**
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich verbinden soll
     * dient der Erzeugung einer beidseitigen Verbindung / ungerichteten Verbindung
     * 
     * @param ip
     * @return 
     * @throws RemoteException
     * @throws AccessException 
     */
    @Override
    public boolean initConnection(String ip) throws RemoteException{           
        try {
            String line;
            BufferedReader bufferedReader;
            //File file = new File("https://1drv.ms/t/s!AjRYgaF5cS41q1BbhwaaWJip_jHP");
            OutputStreamWriter fileOut;
            URL url = new URL("https://1drv.ms/t/s!AjRYgaF5cS41q1Fuz38Cr_X-rBka");
            String[] words;
            StringBuffer inputBuffer = new StringBuffer();
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            
            
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, ip);
            this.serverDaten.connectionList.add(verbindung);
            new VerbindungstestsThread(this.serverDaten, verbindung).start();
            System.out.println("Dauerhafte Verbindung zu Server " + ip + " hergestellt!");
            
           
            while ( (line = bufferedReader.readLine()) != null){
                words = line.split(" ");
                if(words[0].equals(serverDaten.ownIP)){

                    line = words[0] + " " + serverDaten.connectionList.size() + " 0" ;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');    
            }

            bufferedReader.close();
            fileOut = new OutputStreamWriter(url.openConnection().getOutputStream());
            fileOut.write(inputBuffer.toString());;
            fileOut.close();
            
            
            return true;
        } catch (NotBoundException | IOException e) {
            return false;
        }
    }


    /**
     * Methode um zu testen, ob noch eine Verbindung zum Server besteht
     * 
     * @param senderIP
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean ping(String senderIP) throws RemoteException {
        for(Verbindung verbindung : serverDaten.connectionList){
            if(verbindung.getIP().equals(senderIP)){
                return true;
            }
        }
        return false;
    }   
    
}
