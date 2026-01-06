package vn.tuannpa.coursefpt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.Order_Course;
import vn.tuannpa.coursefpt.repository.OrderCourseRepository;

@Service
public class OrderCourseService  {
    private final OrderCourseRepository orderCourseRepository;

    public OrderCourseService(OrderCourseRepository orderCourseRepository) {
        this.orderCourseRepository = orderCourseRepository;
    }

    public Order_Course handleSavOrder_Course(Order_Course order_Course) {
        Order_Course order_Course1 = this.orderCourseRepository.save(order_Course);
        return order_Course1;
    }

    public List<Order_Course> getAllCourseByOrderId(Order order) {
        return this.orderCourseRepository.findByOrder(order);
    }

    public Order_Course findByOrderAndCourse(Order order, Course course) {
        return this.orderCourseRepository.findByOrderAndCourse(order, course);
    }
}
