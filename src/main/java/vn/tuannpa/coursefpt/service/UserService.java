package vn.tuannpa.coursefpt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleSaveUser(User user) {
        User newUser = this.userRepository.save(user);
        return user;
    }

    
}
