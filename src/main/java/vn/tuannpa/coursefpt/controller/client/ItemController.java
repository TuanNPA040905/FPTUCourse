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

import org.springframework.web.bind.annotation.RequestParam;

import vn.tuannpa.coursefpt.domain.Lesson;
import vn.tuannpa.coursefpt.service.LessonService;


@Controller
public class ItemController {

    private final CartDetailRepository cartDetailRepository;
    private final CourseService courseService;
    private final OrderService orderService;
    private final OrderCourseService orderCourseService;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final LessonService lessonService;
    

    public ItemController(CourseService courseService, 
        OrderService orderService, 
        OrderCourseService orderCourseService, 
        CartRepository cartRepository, 
        CartDetailRepository cartDetailRepository,
        UserService userService,
    LessonService lessonService) {
        this.courseService = courseService;
        this.orderService = orderService;
        this.orderCourseService = orderCourseService;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.lessonService = lessonService;
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

    @PostMapping("/add-product-from-view-detail/{id}")
    public String handleAddCourseFromCourseDetailToCart(Model model, @PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        this.courseService.handleAddCourseToCart(email, id, session);
        return "redirect:/";
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
        return "client/my-courses/mycourse";
    }

    @GetMapping("/my-course/{id}")
    public String getMethodName(Model model, 
        @PathVariable long id,
        HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long user_id = (Long)session.getAttribute("id");
        if(user_id == null) {
            return "redirect:/login";
        }
        boolean check_permiss = this.courseService.canAccessCourse(user_id, id);
        if(!check_permiss) {
            return "redirect:/";
        } 

         Optional<Course> course = this.courseService.findById(id);
        if (course.get() == null) {
            return "redirect:/my-courses";
        }

        List<Lesson> lessons = this.courseService.getLessonsByCourseId(id);
        model.addAttribute("lessons", lessons);
        model.addAttribute("course", course.get());

        return "client/my-courses/detail";

    }

    @GetMapping("/my-course/{courseId}/lesson/{lessonId}")
public String showLesson(@PathVariable long courseId,
                         @PathVariable long lessonId,
                         Model model,
                         HttpServletRequest request) {
    // Kiểm tra đăng nhập
    HttpSession session = request.getSession(false);
    Long userId = (Long) session.getAttribute("id");
    if (userId == null) {
        return "redirect:/login";
    }
    
    // Kiểm tra quyền truy cập
    boolean checkPermission = this.courseService.canAccessCourse(userId, courseId);
    if (!checkPermission) {
        return "redirect:/";
    }
    
    // Lấy thông tin bài giảng
    Optional<Lesson> lessonOptional = this.lessonService.findById(lessonId);
    if (!lessonOptional.isPresent()) {
        return "redirect:/my-course/" + courseId;
    }
    Lesson lesson = lessonOptional.get();
    
    // Lấy thông tin khóa học
    Optional<Course> courseOptional = this.courseService.findById(courseId);
    if (!courseOptional.isPresent()) {
        return "redirect:/my-courses";
    }
    Course course = courseOptional.get();
    
    // Lấy tất cả bài giảng (để hiển thị sidebar)
    List<Lesson> allLessons = this.courseService.getLessonsByCourseId(courseId);
    
    // Tìm bài trước và bài sau
    Lesson previousLesson = null;
    Lesson nextLesson = null;
    
    for (int i = 0; i < allLessons.size(); i++) {
        if (allLessons.get(i).getId() == lessonId) {
            if (i > 0) {
                previousLesson = allLessons.get(i - 1);
            }
            if (i < allLessons.size() - 1) {
                nextLesson = allLessons.get(i + 1);
            }
            break;
        }
    }
    
    // Truyền data sang view
    model.addAttribute("lesson", lesson);
    model.addAttribute("course", course);
    model.addAttribute("allLessons", allLessons);
    model.addAttribute("previousLesson", previousLesson);
    model.addAttribute("nextLesson", nextLesson);
    
    return "client/my-courses/lesson"; // Hoặc đường dẫn view của bạn
}
    

}
