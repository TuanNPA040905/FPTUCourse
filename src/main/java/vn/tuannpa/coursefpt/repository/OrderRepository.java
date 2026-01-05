package vn.tuannpa.coursefpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.User;

@Repository
public interface  OrderRepository extends JpaRepository<Order, Long>{
    public List<Order> getOrderByUser(User user);
}
