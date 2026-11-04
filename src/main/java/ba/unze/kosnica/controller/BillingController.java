package ba.unze.kosnica.controller;

import ba.unze.kosnica.model.Subscription;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.BillingService;
import ba.unze.kosnica.service.StripeService;
import ba.unze.kosnica.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Value("${kosnica.base-url}")
    private String baseUrl;

    private final UserService userService;
    private final StripeService stripeService;
    private final BillingService billingService;


    public BillingController(UserService userService, StripeService stripeService, BillingService billingService) {
        this.userService = userService;
        this.stripeService = stripeService;
        this.billingService = billingService;
    }

    @GetMapping
    public String billingPage(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        Subscription subscription = billingService.getFor(user);
        model.addAttribute("user", user);
        model.addAttribute("subscription", subscription);
        model.addAttribute("hasAccess", billingService.hasActiveAccess(user));
        model.addAttribute("daysLeft", billingService.trialDaysLeft(subscription));
        return "billing";
    }

    @PostMapping("/start-trial")
    public RedirectView startTrial(Principal principal) {
        User user = userService.getByEmail(principal.getName());

        String checkoutUrl = stripeService.createCheckoutSession(user.getStripeCustomerId(), baseUrl);
        return new RedirectView(checkoutUrl);
    }
}