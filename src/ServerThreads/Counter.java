package ServerThreads;

public class Counter {
    // Variablen Deklaration und Initialisierung
    private int value;
    
    /**
     * Konstruktur
     */
    public Counter(){
        this.value = 3;
    }
    
    /**
     * Methode, zum bekommen des Wertes innerhalb eines Pings
     * @return 
     */
    public int getValue(){
        return this.value;
    }
    
    /**
     * Methode zum zuruecksetzen eines Counters
     * Bsp. wenn eine Verbindung kurzzeitg den Ping unterbricht und wieder neu anfaengt
     * 
     */
    public void resetCounter(){
        value = 3;
    }
    
    /**
     * Methode, die bei Verbindungsabbau eines Servers, den Counter
     * per Ping um Eins verringert
     */
    public void decrement(){
        value--;
    }
    
    /**
     * Methode, die den Counter/ Ping auf Null setzt
     */
    public void setZero(){
        value = 0;
    }
}
