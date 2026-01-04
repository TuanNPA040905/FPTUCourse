package vn.tuannpa.coursefpt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Cart;
import vn.tuannpa.coursefpt.domain.CartDetail;
import vn.tuannpa.coursefpt.domain.Course;


@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>{
    Optional<CartDetail> findById(long id);

    boolean existsByCartAndCourse(Cart cart, Course course);

    CartDetail findByCartAndCourse(Cart cart, Course realCourse);
}
