package vn.tuannpa.coursefpt.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.UserRepository;

@Controller
public class DashBoardController {
    private final UserRepository userRepository;

    public DashBoardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("/admin")
    public String getDashBoard(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        long id = (Long) session.getAttribute("id");
        User user = this.userRepository.findById(id);
        model.addAttribute("userCheck", user);
        return "admin/dashboard/show";
    }
}
