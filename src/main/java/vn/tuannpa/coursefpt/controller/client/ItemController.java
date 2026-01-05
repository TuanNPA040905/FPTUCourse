package vn.tuannpa.coursefpt.controller.client;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import vn.tuannpa.coursefpt.domain.Cart;
import vn.tuannpa.coursefpt.domain.CartDetail;
import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.Order_Course;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.CartDetailRepository;
import vn.tuannpa.coursefpt.repository.CartRepository;
import vn.tuannpa.coursefpt.repository.OrderCourseRepository;
import vn.tuannpa.coursefpt.service.CourseService;
import vn.tuannpa.coursefpt.service.OrderCourseService;
import vn.tuannpa.coursefpt.service.OrderService;
import vn.tuannpa.coursefpt.service.UserService;

@Controller
public class ItemController {

    private final CartDetailRepository cartDetailRepository;
    private final CourseService courseService;
    private final OrderService orderService;
    private final OrderCourseService orderCourseService;
    private final CartRepository cartRepository;
    private final UserService userService;
    

    public ItemController(CourseService courseService, 
        OrderService orderService, 
        OrderCourseService orderCourseService, 
        CartRepository cartRepository, 
        CartDetailRepository cartDetailRepository,
        UserService userService) {
        this.courseService = courseService;
        this.orderService = orderService;
        this.orderCourseService = orderCourseService;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
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


    @PostMapping("/check-out")
    public String getCheckOutPage(Model model,
        HttpServletRequest request
    ) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        long id = (Long)session.getAttribute("id");
        currentUser.setId(id);
        Cart cart = this.courseService.fetchByUser(currentUser);

        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();

        double totalPrice = 0;
        for (CartDetail cd : cartDetails) {
            totalPrice += cd.getPrice();
        }

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        return "client/cart/checkout";
    }

    @PostMapping("/place-order")
    public String handleOrderCourse(Model model, 
        HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            long id = (Long) session.getAttribute("id");
            User currentUser = new User();
            currentUser.setId(id);
            Cart curCart = this.courseService.fetchByUser(currentUser);
            List<CartDetail> cartDetails = curCart == null ? new ArrayList<CartDetail>() : curCart.getCartDetails();
            double totalPrice = 0;
            for (CartDetail cd: cartDetails) {
                totalPrice += cd.getPrice();
            }
            
            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("Complete");
            order.setTotal_price(totalPrice);
            order.setUser(currentUser);
            Order newOrder = this.orderService.handleSaveOrder(order);

            List<CartDetail> cartDetailsCopy = new ArrayList<>(cartDetails);
            for (CartDetail cd : cartDetailsCopy) {
                Order_Course order_Course = new Order_Course();
                order_Course.setPrice_at_purchase(cd.getPrice());
                order_Course.setCourse(cd.getCourse());
                order_Course.setOrder(newOrder);
                this.orderCourseService.handleSavOrder_Course(order_Course);
            }
            for (CartDetail cartDetail : cartDetailsCopy) {
                this.cartDetailRepository.deleteById(cartDetail.getId());
            }
            session.setAttribute("sum", 0);
            return "client/cart/show";
    }

    @GetMapping("/my-courses")
    public String getMyCoursePage(Model model,
        HttpServletRequest request 
    ) {
        HttpSession session = request.getSession(false);
        long id = (Long) session.getAttribute("id");
        User user = this.userService.getUserById(id);
        List<Order> orders = this.orderService.getMyOrder(user);
        List<Order_Course> order_Courses = new ArrayList<Order_Course>();
        for (Order ods : orders) {
            order_Courses = this.orderCourseService.getAllCourseByOrderId(ods);
        }
        List<Course> courses = new ArrayList<Course>();
        for (Order_Course oc : order_Courses) {
            Optional<Course> course = this.courseService.findById(oc.getCourse().getId());
            if(course.isPresent()) {
                courses.add(course.get());
            }
        }
        if(courses != null) {
            model.addAttribute("courses", courses);
        }
        return "client/homepage/mycourse";
    }

}
