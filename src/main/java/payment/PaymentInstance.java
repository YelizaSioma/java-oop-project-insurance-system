package payment;

import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentInstance implements Comparable<PaymentInstance>{
    //attributes
    private static long NEXT_ID = 0;
    private final long id;
    private final LocalDateTime paymentTime;
    private final int paymentAmount;

    //constructor
    public PaymentInstance(LocalDateTime paymentTime, int paymentAmount){
        //validation
        if (paymentTime == null) {
            throw new IllegalArgumentException("Payment time must be set up in the Payment Instance.");
        }
        if (paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be set up in the Payment Instance.");
        }

        this.paymentTime=paymentTime;
        this.paymentAmount=paymentAmount;
        this.id = NEXT_ID++;
    }

    //methods
    public LocalDateTime getPaymentTime(){
        return paymentTime;
    }

    public int getPaymentAmount(){
        return paymentAmount;
    }

    @Override
    public int compareTo(PaymentInstance o) {
        if (o == null) {
            throw new NullPointerException("Cannot compare to null Payment Instance.");
        }
        return this.paymentTime.compareTo(o.paymentTime);
    }
}
