package vn.tuannpa.coursefpt.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import vn.tuannpa.coursefpt.service.Validation.RegisterChecked;

@RegisterChecked
public class RegisterDTO {
    @NotBlank(message="First name không được để trống")
    @Size(min = 2, max = 30, message = "First name phải từ 2-30 ký tự")
    private String firstName;
    @NotBlank(message="Last name không được để trống")
    @Size(min = 2, max = 30, message = "Last name phải từ 2-30 ký tự")
    private String lastName;
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ!")
    private String email;
    private String password;
    @NotBlank(message="Confirm password không được để trống")
    private String confirmPassword;

    // Getters and setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
}
