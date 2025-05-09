package objects;

import contracts.AbstractContract;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Person {
    //attributes
    private final String id;
    private final LegalForm legalForm;
    private int paidOutAmount;
    private final Set<AbstractContract> contracts;

    //constructor
    public Person(String id){
        //validation
        validatePersonId(id);

        if (isValidBirthNumber(id)) {
            this.legalForm = LegalForm.NATURAL;
        } else if (isValidRegistrationNumber(id)) {
            this.legalForm = LegalForm.LEGAL;
        } else {
            throw new IllegalArgumentException("ID must be a valid birth number or a valid registration number");
        }

        this.id=id;
        this.paidOutAmount = 0;
        this.contracts = new LinkedHashSet<>();
    }

    //additional validation helper methods
    private void validatePersonId(String id){
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID of a person cannot be null or empty");
        }
    }

    //Check if date is valid historical date
    private static boolean isValidDate(int year, int month, int day) {
        try {
            LocalDate date = LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //methods
    public static boolean isValidBirthNumber(String birthNumber){
        //not null
        if (birthNumber == null) {
            return false;
        }
        //9 or 10 chars
        int length = birthNumber.length();
        if (length != 9 && length != 10){
            return false;
        }
        //only numbers
        if (!birthNumber.matches("\\d+")) {
            return false;
        }
        //format  RRMMDDNNN or RRMMDDNNNN
        //extract year, month, day
        int yy = Integer.parseInt(birthNumber.substring(0, 2));
        int mm = Integer.parseInt(birthNumber.substring(2, 4));
        int dd = Integer.parseInt(birthNumber.substring(4, 6));

        //check if month is valid (1-12 for men, 51-62 for women)
        if ((mm < 1 || mm > 12) && (mm < 51 || mm > 62)) {
            return false;
        }

        //adjust month if it's a woman birth number
        int realMonth = (mm > 50) ? mm - 50 : mm;

        //validation for 9-digit birth numbers (before 1954)
        if (length == 9) {
            if (yy > 53) {
                return false;
            }

            //adjust yy
            int year = 1900 + yy;

            //check if date is valid
            return isValidDate(year, realMonth, dd);
        }

        //validation for 10-digit birth numbers (from 1954)
        if (length == 10) {
            //calculate checksum
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                int digit = Character.getNumericValue(birthNumber.charAt(i));
                sum += ((i % 2 == 0) ? 1 : -1) * digit;
            }

            if (sum % 11 != 0) {
                return false;
            }

            //adjust yy
            int year = (yy < 54) ? 2000 + yy : 1900 + yy;

            //check if date is valid
            return isValidDate(year, realMonth, dd);
        }

        return false;
    }

    public static boolean isValidRegistrationNumber(String registrationNumber){
        //not null
        if (registrationNumber == null) {
            return false;
        }
        //6 or 8 chars
        int length = registrationNumber.length();
        if (length != 6 && length != 8){
            return false;
        }
        //contains only numbers
        return registrationNumber.matches("\\d+");
    }

    public String getId(){
        return id;
    }

    public int getPaidOutAmount(){
        return paidOutAmount;
    }

    public LegalForm getLegalForm(){
        return legalForm;
    }

    public Set<AbstractContract> getContracts(){
        return contracts;
    }

    public void addContract(AbstractContract contract){
        if (contract == null) {
            throw new IllegalArgumentException("Contract can't be null");
        }
        contracts.add(contract);
    }

    public void payout(int paidOutAmount){
        if (paidOutAmount <= 0) {
            throw new IllegalArgumentException("Paid out amount must be positive");
        }
        this.paidOutAmount += paidOutAmount;
    }
}
