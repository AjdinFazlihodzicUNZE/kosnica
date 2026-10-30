package ba.unze.kosnica.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Unesi ime i prezime")
    private String fullName;

    @NotBlank(message = "Unesi email")
    @Email(message = "Email nije ispravan")
    private String email;

    @Size(min = 6, message = "Lozinka mora imati najmanje 6 znakova")
    private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}