package ba.unze.kosnica.controller;

import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.StripeService;
import ba.unze.kosnica.service.UserService;
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

    private final UserService userService;
    private final StripeService stripeService;

    public BillingController(UserService userService, StripeService stripeService) {
        this.userService = userService;
        this.stripeService = stripeService;
    }

    @GetMapping
    public String billingPage(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("user", user);
        return "billing";
    }

    @PostMapping("/start-trial")
    public RedirectView startTrial(Principal principal) {
        User user = userService.getByEmail(principal.getName());

       
        String domain = "http://localhost:8080";

        String checkoutUrl = stripeService.createCheckoutSession(user.getStripeCustomerId(), domain);
        return new RedirectView(checkoutUrl);
    }
}