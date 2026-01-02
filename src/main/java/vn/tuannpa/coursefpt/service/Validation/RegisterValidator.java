package vn.tuannpa.coursefpt.service.Validation;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.tuannpa.coursefpt.domain.dto.RegisterDTO;
import vn.tuannpa.coursefpt.service.UserService;

@Service
public class RegisterValidator implements ConstraintValidator<RegisterChecked, RegisterDTO>{
    private final UserService userService;
    
    public RegisterValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(RegisterDTO user, ConstraintValidatorContext context) {
        boolean valid = true;

        if(!user.getPassword().equals(user.getConfirmPassword())) {
            context.buildConstraintViolationWithTemplate("Passwords nhập không chính xác!").addPropertyNode("confirmPassword").addConstraintViolation().disableDefaultConstraintViolation();
            valid = false;
        }

        // Additional validations can be added here
        //check email

        if(this.userService.checkEmailExist(user.getEmail())) {
            System.out.println("password đã exist");
            context.buildConstraintViolationWithTemplate("Email đã tồn tại!").addPropertyNode("email").addConstraintViolation().disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
