package vn.tuannpa.coursefpt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{
    List<Course> findAll();


    Course save(Course course);

    void deleteById(long id);

    Optional<Course> findById(long id);
    
    Page<Course> findAll(Pageable page);

    Page<Course> findAll(Specification<Course> spec, Pageable page);
}
