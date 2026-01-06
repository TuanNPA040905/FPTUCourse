package vn.tuannpa.coursefpt.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.tuannpa.coursefpt.domain.Lesson;
import vn.tuannpa.coursefpt.repository.LessonRepository;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Optional<Lesson> findById(long id) {
        return this.lessonRepository.findById(id);
    }
}
