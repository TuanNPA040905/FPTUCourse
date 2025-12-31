package vn.tuannpa.coursefpt.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.RoleRepository;
import vn.tuannpa.coursefpt.service.UploadService;
import vn.tuannpa.coursefpt.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, UploadService uploadService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }



    @GetMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/show";
    }

    @GetMapping("admin/user/create")
    public String getCreateUserPage(Model model) {
        User newUser = new User();
        model.addAttribute("newUser", newUser);
        return "admin/user/create";
    }

    @PostMapping("admin/user/create")
    public String handleCreateUser(Model model,
            @ModelAttribute("newUser") @Valid User newUser,
            BindingResult newUserBindingResult,
            @RequestParam("avaFile") MultipartFile file
    ) {
        if(newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }

        String avatar = this.uploadService.handSaveUpLoadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());

        newUser.setAvatar(avatar);
        newUser.setPassword(hashPassword);
        newUser.setRole(this.roleRepository.findByName(newUser.getRole().getName()));
        System.out.print(newUser);
        this.userService.handleSaveUser(newUser);
        return "redirect:/admin/user";
    }
}
