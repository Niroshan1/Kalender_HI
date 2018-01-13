/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

/**
 *
 * @author timtim
 */
public class Counter {
    
    private int value;
    
    public Counter(){
        this.value = 0;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public void resetCounter(){
        value = 3;
    }
    
    public void increment(){
        value++;
    }
    
    public void setNegativ(){
        value = -1;
    }
}
