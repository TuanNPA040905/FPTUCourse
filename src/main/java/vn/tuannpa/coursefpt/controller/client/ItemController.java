package vn.tuannpa.coursefpt.controller.client;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.tuannpa.coursefpt.domain.Cart;
import vn.tuannpa.coursefpt.domain.CartDetail;
import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.service.CourseService;

@Controller
public class ItemController {
    private final CourseService courseService;
    

    public ItemController(CourseService courseService) {
        this.courseService = courseService;
    }
    
    @GetMapping("/course/{id}")
    public String getCourseDetailPage(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id).get();
        model.addAttribute("course", course);
        model.addAttribute("id", id);
        return "client/course/details";
    }


    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);
        Cart cart = this.courseService.fetchByUser(currentUser);

        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();

        double totalPrice = 0;
        for (CartDetail cd : cartDetails) {
            totalPrice += cd.getPrice();
        }

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cart", cart);
        return "client/cart/show";
    }

    @PostMapping("/add-course-to-cart/{id}")
    public String handleAddCourseToCart(Model model, 
        @PathVariable long id,
        HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String)session.getAttribute("email");
        this.courseService.handleAddCourseToCart(email, id, session);
        return "redirect:/";
    }


    @PostMapping("/delete-favorite-course/{id}")
    public String handleDeleteCourseInCart(Model model,
        @PathVariable long id,
        HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        long cartDetailId = id;
        this.courseService.handleRemoveCartDetail(cartDetailId, session);
        return "redirect:/cart";

    }

}
