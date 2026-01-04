package vn.tuannpa.coursefpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{
    List<Course> findAll();


    Course save(Course course);

    void deleteById(long id);
    
}
