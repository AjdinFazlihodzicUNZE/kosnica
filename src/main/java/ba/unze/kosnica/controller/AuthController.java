package ba.unze.kosnica.controller;

import ba.unze.kosnica.form.RegisterForm;
import ba.unze.kosnica.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}