package vn.tuannpa.coursefpt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.tuannpa.coursefpt.domain.Cart;
import vn.tuannpa.coursefpt.domain.CartDetail;
import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Lesson;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.CartDetailRepository;
import vn.tuannpa.coursefpt.repository.CartRepository;
import vn.tuannpa.coursefpt.repository.CourseRepository;
import vn.tuannpa.coursefpt.repository.LessonRepository;
import vn.tuannpa.coursefpt.repository.OrderRepository;

@Service
public class CourseService {

    private final CartDetailRepository cartDetailRepository;

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderCourseService orderCourseService;
    private final LessonRepository lessonRepository;

    public CourseService(CourseRepository courseRepository,
         CartRepository cartRepository,
         UserService userService,
        CartDetailRepository cartDetailRepository,
        OrderRepository orderRepository,
    OrderCourseService orderCourseService,
LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.cartDetailRepository = cartDetailRepository;
        this.orderRepository = orderRepository;
        this.orderCourseService = orderCourseService;
        this.lessonRepository = lessonRepository;
    }


    public List<Course> getAllCourses() {
        return this.courseRepository.findAll();
    }

    public Optional<Course> getCourseById(long id) {
        return this.courseRepository.findById(id);
    }

    public Course handSaveCourse(Course course) {
        return this.courseRepository.save(course);
    }

    public void handleDeleteCourse(long id) {
        this.courseRepository.deleteById(id);
    }

    public Page<Course> fetchCourses(Pageable pageable) {
        return this.courseRepository.findAll(pageable);
    }

    public Cart fetchByUser(User user) {
        return (Cart)this.cartRepository.findByUser(user);
    }

    public void handleAddCourseToCart(String email, long id, HttpSession session ) {
        User user = this.userService.findByEmail(email);
        if(user != null) {
            Cart cart = (Cart)this.cartRepository.findByUser(user);
            if(cart == null) {
                Cart otherCart = new Cart();
                otherCart.setSum(0);
                otherCart.setUser(user);
                cart = this.cartRepository.save(otherCart);
            }

            Optional<Course> courseOptional = this.courseRepository.findById(id);
           if(courseOptional.isPresent()) {
                Course realCourse = courseOptional.get();

                //check sản phẩm đã từng được thêm vào giỏ hàng trước đây chưa ?
                CartDetail oldDetail = this.cartDetailRepository.findByCartAndCourse(cart, realCourse);

                if(oldDetail == null) {
                    CartDetail cd = new CartDetail();
                    cd.setCart(cart);
                    cd.setCourse(realCourse);
                    cd.setPrice(realCourse.getPrice());
                    this.cartDetailRepository.save(cd);

                    //update cart (sum)
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    this.cartDetailRepository.save(oldDetail);
                }
            }
        }
    }

    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if(cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();

            Cart currentCart = cartDetail.getCart();
            this.cartDetailRepository.deleteById(cartDetailId);
            if(currentCart.getSum() > 1) {
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currentCart);
            } else {
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);
            }   
        }
    }

    public Optional<Course> getMyCourses(long id) {
        return this.courseRepository.findById(id);
    }

    public Optional<Course> findById(long id) {
        return this.courseRepository.findById(id);
    }

    public boolean canAccessCourse(long userId, long id) {
        User user = this.userService.getUserById(userId);
        Optional<Course> courseOptional = this.courseRepository.findById(id);
        if(!courseOptional.isPresent()) return false;
        if(user == null) return false;
        List<Order> ods = this.orderRepository.getOrderByUser(user);
        for (Order order : ods) {
            
            if(this.orderCourseService.findByOrderAndCourse(order, courseOptional.get()) != null) {
                return true;
            }
        }
        return false;
    }


    public List<Lesson> getLessonsByCourseId(long id) {
        Optional<Course> courseOptional = this.courseRepository.findById(id);
        return this.lessonRepository.getLessonsByCourse(courseOptional.get());
    }
}
