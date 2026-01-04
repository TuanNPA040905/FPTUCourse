package vn.tuannpa.coursefpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Cart;
import vn.tuannpa.coursefpt.domain.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
    Cart findByUser(User user);
    
}
