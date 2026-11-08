package ba.unze.kosnica.controller;

import ba.unze.kosnica.config.StripeConfig;
import ba.unze.kosnica.model.Subscription;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.SubscriptionRepository;
import ba.unze.kosnica.repository.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private final StripeConfig stripeConfig;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public StripeWebhookController(StripeConfig stripeConfig,
                                   UserRepository userRepository,
                                   SubscriptionRepository subscriptionRepository) {
        this.stripeConfig = stripeConfig;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @SuppressWarnings("RequestHeader")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nedostaje Stripe-Signature zaglavlje");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Neispravan potpis webhooka");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        switch (event.getType()) {
            case "customer.subscription.created":
            case "customer.subscription.updated":
            case "customer.subscription.deleted":
                if (stripeObject instanceof com.stripe.model.Subscription stripeSub) {
                    handleSubscriptionChange(stripeSub);
                }
                break;
            default:
                break;
        }

        return ResponseEntity.ok("Primljeno");
    }

    private void handleSubscriptionChange(com.stripe.model.Subscription stripeSub) {
        String customerId = stripeSub.getCustomer();
        User user = userRepository.findByStripeCustomerId(customerId).orElse(null);

        if (user == null) {
            return;
        }

        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElse(new Subscription());

        subscription.setUser(user);
        subscription.setStripeSubscriptionId(stripeSub.getId());
        subscription.setStatus(stripeSub.getStatus());

        if (stripeSub.getTrialEnd() != null) {
            subscription.setTrialEnd(LocalDateTime.ofInstant(Instant.ofEpochSecond(stripeSub.getTrialEnd()), ZoneId.systemDefault()));
        }


        Long currentPeriodEnd = stripeSub.getItems() != null && !stripeSub.getItems().getData().isEmpty()
                ? stripeSub.getItems().getData().get(0).getSubscription() != null ? stripeSub.getTrialEnd() : null
                : null;

        if (currentPeriodEnd != null) {
            subscription.setCurrentPeriodEnd(LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodEnd), ZoneId.systemDefault()));
        }

        subscriptionRepository.save(subscription);
    }
}