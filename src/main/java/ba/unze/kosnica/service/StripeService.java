package ba.unze.kosnica.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.price-id:}")
    private String priceId;

    public String createCustomer(String email, String fullName) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(fullName)
                    .build();
            Customer customer = Customer.create(params);
            return customer.getId();
        } catch (StripeException e) {
            throw new IllegalStateException("Stripe error: " + e.getMessage(), e);
        }
    }

    public String createCheckoutSession(String customerId, String domain) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setCustomer(customerId)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(domain + "/billing?success=true")
                    .setCancelUrl(domain + "/billing?canceled=true")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .build()
                    )
                    .setSubscriptionData(
                            SessionCreateParams.SubscriptionData.builder()
                                    .setTrialPeriodDays(14L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new IllegalStateException("Stripe Checkout greška: " + e.getMessage(), e);
        }
    }
}