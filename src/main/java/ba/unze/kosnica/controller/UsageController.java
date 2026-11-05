package ba.unze.kosnica.controller;

import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.UsageService;
import ba.unze.kosnica.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UsageController {

    private static final double PRICE_PER_MB = 0.02;

    private final UsageService usageService;
    private final UserService userService;

    public UsageController(UsageService usageService, UserService userService) {
        this.usageService = usageService;
        this.userService = userService;
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
}