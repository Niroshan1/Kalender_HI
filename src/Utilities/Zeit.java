/*
 * ~~~ erstmal fertig ~~~
 * 
 * 
 */
package Utilities;

import java.io.Serializable;

/**
 *
 * @author Tim Meyer
 */
public class Zeit implements Serializable{
    private int stunde;
    private int minute;
    
    public Zeit(int stunde, int minute) throws ZeitException{
        if(stunde > 23 || stunde < 0){
            throw new ZeitException("Stunde zwischen 0 und 23 wählen");
        }
        this.stunde = stunde;
      
        if(minute > 59 || minute < 0){
            throw new ZeitException("Minute zwischen 0 und 59 wählen");
        }
        this.minute = minute;
    }

    //Setter:
    public void setStunde(int stunde) throws ZeitException{
        if(stunde > 23 || stunde < 0){
            throw new ZeitException("Stunde zwischen 0 und 23 wählen");
        }
        this.stunde = stunde;
    }
    public void setMinute(int minute) throws ZeitException{
        if(minute > 59 || minute < 0){
            throw new ZeitException("Minute zwischen 0 und 59 wählen");
        }
        this.minute = minute;
    }
    
    //Getter:
    public int getStunde(){
        return stunde;
    }
    public int getMinute(){
        return minute;
    }
    
    @Override
    public String toString(){
        if(this.minute < 10){
            return this.stunde + ":0" + this.minute;
        }
        return this.stunde + ":" + this.minute;
    }
    
    /**
     * Exception-Klasse für Klasse Zeit
     */
    public static class ZeitException extends Exception implements Serializable{
        
        private final String message;
        
        public ZeitException(String message) {
            this.message = message;
        }
        
        @Override
        public String getMessage(){
            return message;
        }
    }
}
