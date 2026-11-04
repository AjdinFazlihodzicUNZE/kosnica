package ba.unze.kosnica.service;

import ba.unze.kosnica.model.Subscription;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BillingService {
    public final SubscriptionRepository subscriptionRepository;

    public BillingService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription getFor(User user) {
        return subscriptionRepository.findByUser(user).orElse(null);
    }

    public boolean hasActiveAccess(User user) {
        Subscription subscription = getFor(user);
        if (subscription == null) {
            return false;
        }
        return "trialing".equals(subscription.getStatus()) || "active".equals(subscription.getStatus());
    }
    public long trialDaysLeft(Subscription subscription) {
        if (subscription == null || subscription.getTrialEnd() == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getTrialEnd());
        return Math.max(days, 0);
    }
}
