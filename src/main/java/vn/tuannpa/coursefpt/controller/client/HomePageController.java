package vn.tuannpa.coursefpt.controller.client;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Role;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.domain.dto.RegisterDTO;
import vn.tuannpa.coursefpt.service.CourseService;
import vn.tuannpa.coursefpt.service.UserService;

@Controller
public class HomePageController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CourseService courseService;

    public HomePageController(PasswordEncoder passwordEncoder, UserService userService, CourseService courseService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> courses = this.courseService.fetchCourses(pageable);
        List<Course> courseList = courses.getContent();
        model.addAttribute("courses", courseList);
        return "client/homepage/show";
    }


    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/courses")
    public String getCoursesPage(Model model) {
        List<Course> courses = this.courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "client/course/show";
    }


    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }


    @PostMapping("/register")
    public String handleRegister(
        @ModelAttribute("registerUser") @Valid RegisterDTO registerDTO, BindingResult result
    ) {
        if(result.hasErrors()) {
            return "client/auth/register";
        }
        User newUser = this.userService.handleRegisterDTOtoUser(registerDTO);
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        Role role = new Role();
        role.setId(2);
        newUser.setRole(role);
        this.userService.handleSaveUser(newUser);
        return "redirect:/login";
    }

}
