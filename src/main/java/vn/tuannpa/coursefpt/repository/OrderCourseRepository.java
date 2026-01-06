package vn.tuannpa.coursefpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Order;
import vn.tuannpa.coursefpt.domain.Order_Course;


@Repository
public interface OrderCourseRepository extends JpaRepository<Order_Course, Long>{


    public List<Order_Course> findByOrder(Order order);

    public Order_Course findByOrderAndCourse(Order order, Course course);
}
