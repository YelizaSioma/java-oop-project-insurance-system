package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;
import payment.ContractPaymentData;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple travel insurance: covers a fixed set of natural persons.
 * The insuredPersons set is never null, never empty, and never changes.
 */
public class TravelContract extends AbstractContract {
    //attributes
    private final Set<Person> insuredPersons;

    /**
     * @param contractPaymentData       non-null
     * @param personsToInsure           non-null, non-empty, only NATURAL persons
     *
     * @throws IllegalArgumentException - in constructor - if any personsToInsure validation fails
     * @throws IllegalArgumentException - in constructor - if contractPaymentData is null
     */
    //constructor
    public TravelContract(String contractNumber,
                          InsuranceCompany insurer,
                          Person policyHolder,
                          ContractPaymentData contractPaymentData,
                          int coverageAmount,
                          Set<Person> personsToInsure){
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);

        //validation
        // ensure contractPaymentData is not null and personsToInsure is not null or empty
        validateTravelContractParams(contractPaymentData, personsToInsure);
        // ensure all insured are natural persons
        validateInsuredPersonsAreNatural(personsToInsure);

        //store the insured persons
        this.insuredPersons = new HashSet<>(personsToInsure);
    }

    //___________Public methods___________
    public Set<Person> getInsuredPersons(){
        return insuredPersons;
    }


    //___________Private helpers___________
    /**
     * @throws IllegalArgumentException if contractPaymentData is null,
     *                                  or personsToInsure is null or empty
     */
    private void validateTravelContractParams(ContractPaymentData contractPaymentData, Set<Person> personsToInsure) {
        if (contractPaymentData == null) {
            throw new IllegalArgumentException("Contract payment data cannot be null for travel contract");
        }
        if (personsToInsure == null || personsToInsure.isEmpty()){
            throw new IllegalArgumentException("Persons to insure cannot be null or empty");
        }
    }

    /**
     * All insured persons must be natural persons.
     *
     * @throws IllegalArgumentException if any person is not NATURAL
     */
    private void validateInsuredPersonsAreNatural(Set<Person> personsToInsure) {
        for (Person person : personsToInsure) {
            if (person.getLegalForm() != LegalForm.NATURAL) {
                throw new IllegalArgumentException("Only natural persons can be insured in travel contract");
            }
        }
    }
}
