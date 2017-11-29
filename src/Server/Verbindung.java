/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author timtim
 */
public class Verbindung {
    
    private final ServerStub stub;
    private final String ip;
    private final int port;
    
    /**
     *
     * @param stub
     * @param ip
     * @param port
     */
    public Verbindung(ServerStub stub, String ip, int port){
        this.stub = stub;
        this.ip = ip;
        this.port = port;
    }
    
    public ServerStub getServerStub(){
        return this.stub;
    }
    
    public String getIP(){
        return this.ip;
    }
    
    public int getPort(){
        return this.port;
    }
    
    public boolean equals(String ip, int port){
        return this.ip.equals(ip) && this.port == port;
    }
    
    
}
