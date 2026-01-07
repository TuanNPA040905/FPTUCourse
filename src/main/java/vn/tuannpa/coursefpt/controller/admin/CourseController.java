package vn.tuannpa.coursefpt.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.tuannpa.coursefpt.domain.Course;
import vn.tuannpa.coursefpt.service.CourseService;
import vn.tuannpa.coursefpt.service.UploadService;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final UploadService uploadService;

    public CourseController(CourseService courseService, UploadService uploadService) {
        this.courseService = courseService;
        this.uploadService = uploadService;
    }

    

    @GetMapping("/admin/course")
    public String getCoursePage(Model model,
        @RequestParam("page") Optional<String> pageOptional
    ) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch(Exception e) {
            System.out.println("Lỗi to");
        }

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Course> pages = this.courseService.getAllCourses(pageable);
        int totalPages = pages.getTotalPages();
        List<Course> courses = pages.getContent();
        model.addAttribute("courses", courses);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("curPage", page);
        return "admin/course/show";
    }

    @GetMapping("/admin/course/detail/{id}")
    public String getCourseDetailPage(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id).get();
        model.addAttribute("course", course);
        return "admin/course/detail";
    }

    @GetMapping("/admin/course/create")
    public String getCreateCoursePage(Model model) {
        Course newCourse = new Course();
        model.addAttribute("newCourse", newCourse);
        return "admin/course/create";
    }

    @PostMapping("/admin/course/create")
    public String postCreateCoursePage(Model model, @ModelAttribute("newCourse") Course newCourse, @RequestParam("imageFile") MultipartFile file) {
        String image = this.uploadService.handSaveUpLoadFile(file, "course");
        newCourse.setImage(image);
        this.courseService.handSaveCourse(newCourse);
        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/{id}") 
    public String getDetailPageCouse(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id).get();
        model.addAttribute("course", course);
        return "admin/course/detail";
    }

    @GetMapping("admin/course/update/{id}")
    public String getUpdateCoursePage(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id).get();
        model.addAttribute("newCourse", course);
        return "admin/course/update";
    }

    @PostMapping("admin/course/update/{id}")
    public String handleUpdateCourse(Model model, 
        @ModelAttribute("newCourse") Course newCourse,
        @PathVariable long id
    ) {
        Course course = this.courseService.getCourseById(id).get();
        course.setTitle(newCourse.getTitle());
        course.setDescription(newCourse.getDescription());
        course.setPrice(newCourse.getPrice());
        course.setActive(newCourse.getActive());
        this.courseService.handSaveCourse(course);
        return "redirect:/admin/course";
    }

    @GetMapping("admin/course/delete/{id}")
    public String getDeleteCoursePage(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id).get();
        model.addAttribute("newCourse", course);
        return "admin/course/delete";
    }

    @PostMapping("admin/course/delete/{id}")
    public String handleDeleteCourse(@PathVariable long id) {
        this.courseService.handleDeleteCourse(id);
        return "redirect:/admin/course";
    }

    
 }
