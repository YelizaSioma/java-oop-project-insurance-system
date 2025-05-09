package objects;

public class Vehicle {
    //attributes
    private final String licensePlate; //EČV
    private final int originalValue; //cena

    //constructor
    public Vehicle(String licensePlate, int originalValue){
        //validation
        validateVehicleParams(licensePlate, originalValue);

    //initialization
        this.licensePlate=licensePlate;
        this.originalValue=originalValue;
    }

    //___________Public methods___________
    public String getLicensePlate(){
        return licensePlate;
    }

    public int getOriginalValue(){
        return originalValue;
    }

    //___________Private helpers___________
    private void validateVehicleParams(String licensePlate, int originalValue) {
        if(licensePlate == null){
            throw new IllegalArgumentException("License plate cannot be null");
        }
        if(licensePlate.length()!=7){
            throw new IllegalArgumentException("License plate must be 7 characters in length");
        }
        // every char must be A–Z or 0–9
        for (char c : licensePlate.toCharArray()) {
            if (!( (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') )) {
                throw new IllegalArgumentException("License plate must contain only uppercase A–Z or digits 0–9");
            }
        }
        if(originalValue <= 0){
            throw new IllegalArgumentException("Original value must be positive number");
        }
    }
}
