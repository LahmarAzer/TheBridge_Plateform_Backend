package com.bridge.elearningplateform.controllers;

import com.bridge.elearningplateform.entities.Course;
import com.bridge.elearningplateform.services.ICourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

    ICourseService iCourseService ;

    @PostMapping("/addCourse")
    public ResponseEntity<Course> CreateCourse(
            @RequestParam String title,
            @RequestParam double price,
            @RequestParam MultipartFile imagePath
    ) {
        try {
            Course course = iCourseService.addCourse(title, price, imagePath);
            return new ResponseEntity<>(course, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updatecourse/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Integer id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Course updatedCourse = iCourseService.updateCourse(id, title, price, image);
            return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Échec de la mise à jour du cours: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur interne lors de la mise à jour du cours: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCourse/{id}")
    public void deleteCourse(@PathVariable Integer id) {
        iCourseService.deleteCourse(id);
    }

    @GetMapping("/getCourse/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return iCourseService.getCourseById(id);
    }

    @GetMapping("/getAllCourses")
    public List<Course> getAllCourses(){
        return iCourseService.getAllCourses();
    }

}
