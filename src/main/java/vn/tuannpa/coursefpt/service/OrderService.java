package vn.tuannpa.coursefpt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.User;
import vn.tuannpa.coursefpt.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order handleSaveOrder(Order order) {
        this.orderRepository.save(order);
        return order;
    }

    public List<Order> getMyOrder(User user) {
        return this.orderRepository.getOrderByUser(user);
    }
}
