package objects;

public class Vehicle {
    //attributes
    private final String licensePlate; //EČV
    private final int originalValue; //cena

    //constructor
    public Vehicle(String licensePlate, int originalValue){
        this.licensePlate=licensePlate;
        this.originalValue=originalValue;
    }

    //methods
    public String getLicensePlate(){
        return licensePlate;
    }

    public int getOriginalValue(){
        return originalValue;
    }
}
