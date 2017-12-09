/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerStub;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class PingThread extends Thread{
    
    private final ServerStub serverStub;
    private final Counter counter;
    
    public PingThread(ServerStub serverStub, Counter counter){
        this.serverStub = serverStub;
        this.counter = counter;
    }    
    
    @Override 
    public void run(){
        try {
            this.serverStub.ping();
            counter.resetCounter();
        } catch (RemoteException ex) {
        }
    }
}
