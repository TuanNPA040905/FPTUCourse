package vn.tuannpa.coursefpt.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.Order_Course;
import vn.tuannpa.coursefpt.repository.OrderCourseRepository;
import vn.tuannpa.coursefpt.repository.OrderRepository;
import vn.tuannpa.coursefpt.service.OrderService;

@Controller
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderCourseRepository orderCourseRepository;
    private final OrderService orderService;
    
    public OrderController(OrderRepository orderRepository, OrderCourseRepository orderCourseRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderCourseRepository = orderCourseRepository;
        this.orderService = orderService;
    }



    @GetMapping("/admin/order")
    public String getUserPage(Model model) {
        List<Order> orders = this.orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getDetailOrder(Model model,
        @PathVariable long id
    ) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if(orderOptional.isPresent()) {
            List<Order_Course> order_Courses = this.orderCourseRepository.findByOrder(orderOptional.get());
            List<Course> courses = new ArrayList<Course>();
            for (Order_Course course : order_Courses) {
                Course course1 = course.getCourse();
                if(course1 != null) {
                    courses.add(course1);
                }
            }
            model.addAttribute("courses", courses);
            model.addAttribute("order", orderOptional.get());
        }
        return "admin/order/detail";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String getDeleteOrderPage(Model model, 
        @PathVariable long id
    ) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        model.addAttribute("order", orderOptional.get());
        return "admin/order/delete";
    }

    @PostMapping("admin/order/delete/{id}")
    public String handleDeleteOrder(Model model,
        @PathVariable long id
    ) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if(orderOptional.isPresent()) {
            this.orderService.handleDeleteOrder(id);
        }
        return "admin/order/show";
    }


}
