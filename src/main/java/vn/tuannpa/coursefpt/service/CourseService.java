package vn.tuannpa.coursefpt.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.repository.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    public List<Course> getAllCourses() {
        return this.courseRepository.findAll();
    }

    public Course getCourseById(long id) {
        return this.courseRepository.findById(id);
    }

    public Course handSaveCourse(Course course) {
        return this.courseRepository.save(course);
    }

    public void handleDeleteCourse(long id) {
        this.courseRepository.deleteById(id);
    }

    public Page<Course> fetchCourses(Pageable pageable) {
        return this.courseRepository.findAll(pageable);
    }
}
