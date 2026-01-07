package vn.tuannpa.coursefpt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.OrderCourseRepository;
import vn.tuannpa.coursefpt.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderCourseRepository orderCourseRepository;

    public OrderService(OrderRepository orderRepository, OrderCourseRepository orderCourseRepository) {
        this.orderRepository = orderRepository;
        this.orderCourseRepository = orderCourseRepository;
    }

    public Order handleSaveOrder(Order order) {
        this.orderRepository.save(order);
        return order;
    }

    public List<Order> getMyOrder(User user) {
        return this.orderRepository.getOrderByUser(user);
    }

    @Transactional
    public void handleDeleteOrder(long id) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if(orderOptional.isPresent()) {
            this.orderCourseRepository.deleteByOrder(orderOptional.get());
            this.orderRepository.deleteById(id);
        }
    }
}
