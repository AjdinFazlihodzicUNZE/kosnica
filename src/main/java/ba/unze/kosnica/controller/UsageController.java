package ba.unze.kosnica.controller;

import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.UsageService;
import ba.unze.kosnica.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.beans.factory.annotation.Value;
import ba.unze.kosnica.service.StripeService;

import java.security.Principal;

@Controller
public class UsageController {

    private static final double PRICE_PER_MB = 0.02;

    @Value("${kosnica.base-url}")
    private String baseUrl;

    private final UsageService usageService;
    private final UserService userService;
    private final StripeService stripeService;

    public UsageController(UsageService usageService, UserService userService,
                           StripeService stripeService) {
        this.usageService = usageService;
        this.userService = userService;
        this.stripeService = stripeService;
    }

    @GetMapping("/usage")
    public String usage(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        long totalMb = usageService.totalMegabytes(user);
        model.addAttribute("records", usageService.listFor(user));
        model.addAttribute("totalMb", totalMb);
        model.addAttribute("estimatedPrice", totalMb * PRICE_PER_MB);
        return "usage";
    }


    @PostMapping("/usage/portal")
    public RedirectView portal(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user.getStripeCustomerId() == null) {
            return new RedirectView("/usage?noCustomer");
        }
        String portalUrl = stripeService.createPortalSession(user.getStripeCustomerId(), baseUrl + "/usage");
        return new RedirectView(portalUrl);
    }
}