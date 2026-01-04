package vn.tuannpa.coursefpt.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.domain.dto.RegisterDTO;
import vn.tuannpa.coursefpt.repository.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleSaveUser(User user) {
        User newUser = this.userRepository.save(user);
        return user;
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public boolean checkEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User loadUserByUsername(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User getUserByName(String email, String rawPassword) {
        User user = this.userRepository.findByEmail(email);
        
        System.out.println("User found: " + (user != null));
        if(user != null) {
            System.out.println("Raw password: " + rawPassword);
            System.out.println("Hashed password: " + user.getPassword());
            System.out.println("Match result: " + passwordEncoder.matches(rawPassword, user.getPassword()));
        }
        
        if(user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User handleRegisterDTOtoUser(RegisterDTO registerDTO) {
        User newUser = new User();
        newUser.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(registerDTO.getPassword());
        return newUser;
    }



    
}
