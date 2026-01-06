package vn.tuannpa.coursefpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.domain.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>{
    public List<Lesson> getLessonsByCourse(Course course);
}
