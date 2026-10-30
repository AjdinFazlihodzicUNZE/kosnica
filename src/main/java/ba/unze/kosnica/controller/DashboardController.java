package ba.unze.kosnica.controller;

import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("user", user);
        return "dashboard";
    }
}