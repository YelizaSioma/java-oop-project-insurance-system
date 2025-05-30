package payment;

import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentInstance implements Comparable<PaymentInstance>{
    //attributes
    private static long NEXT_ID = 0;
    private final long id;
    private final LocalDateTime paymentTime;
    private final int paymentAmount;

    /**
     * @param paymentTime   must be non-null
     * @param paymentAmount must be > 0
     * @throws IllegalArgumentException if validation fails
     */
    //constructor
    public PaymentInstance(LocalDateTime paymentTime, int paymentAmount){
        //validation
        validateConstructorParams(paymentTime, paymentAmount);

        this.paymentTime=paymentTime;
        this.paymentAmount=paymentAmount;
        this.id = NEXT_ID++;
    }

    //___________Public methods___________
    public LocalDateTime getPaymentTime(){
        return paymentTime;
    }

    public int getPaymentAmount(){
        return paymentAmount;
    }

    //___________Private helpers___________
    private void validateConstructorParams(LocalDateTime paymentTime, int paymentAmount) {
        if (paymentTime == null) {
            throw new IllegalArgumentException("Payment time must be set up in the Payment Instance.");
        }
        if (paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be set up in the Payment Instance.");
        }
    }


    //___________Override methods___________
    @Override
    public int compareTo(PaymentInstance o) {
        int cmp = this.getPaymentTime().compareTo(o.getPaymentTime());
        if (cmp != 0) return cmp;
        return Long.compare(this.id, o.id);
    }
}
