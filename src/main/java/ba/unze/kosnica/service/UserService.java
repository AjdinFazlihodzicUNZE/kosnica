package ba.unze.kosnica.service;

import ba.unze.kosnica.form.RegisterForm;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StripeService stripeService;

    private static final Logger log = (Logger) LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, StripeService stripeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.stripeService = stripeService;
    }

    public User register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Ovaj email je već registrovan");
        }
        User user = new User();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        try {
            String customerId = stripeService.createCustomer(user.getEmail(),user.getFullName());
            user.setStripeCustomerId(customerId);
            user = userRepository.save(user);
        } catch(IllegalStateException e) {
            log.error("Nije bilo moguce napraviti Stripe profil za {}: {}", user.getEmail(), e.getMessage());
        }
        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Korisnik nije pronađen"));
    }
}