package com.bridge.elearningplateform.services;

import com.bridge.elearningplateform.entities.Course;
import com.bridge.elearningplateform.repositories.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ICourseIMP implements ICourseService{

    @Value("${file.upload}")
    private String pathFile;

    @Autowired
    private CourseRepository courseRepository;


    public boolean addFile(MultipartFile file) {
        try {
            File convertFile = new File(pathFile + file.getOriginalFilename());
            convertFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(convertFile);
            fout.write(file.getBytes());
            fout.close();
            log.info("Fichier '{}' téléchargé avec succès.", file.getOriginalFilename());
            return true;
        } catch (Exception e) {
            log.error("Échec de l'upload du fichier '{}': {}", file.getOriginalFilename(), e.getMessage());
            return false;
        }
    }

    public Course addCourse(String title, double price, MultipartFile image) {
        boolean fileAdded = addFile(image);
        if (!fileAdded) {
            log.error("Échec de la sauvegarde de l'image pour le cours '{}'.", title);
            throw new RuntimeException("Erreur lors de la sauvegarde de l'image.");
        }
        String imagePath = pathFile + image.getOriginalFilename();
        Course course = new Course();
        course.setTitle(title);
        course.setPrice(price);
        course.setImagePath(imagePath);
        Course savedCourse = courseRepository.save(course);
        log.info("Cours '{}' ajouté avec succès avec l'ID {}.", title, savedCourse.getId());
        return savedCourse;
    }

    @Override
    public Course updateCourse(Integer id, String title, double price, MultipartFile image) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        boolean fileAdded = true;
        String imagePath = course.getImagePath();

        if (image != null && !image.isEmpty()) {
            fileAdded = addFile(image);
            imagePath = pathFile + image.getOriginalFilename();
        }

        if (!fileAdded) {
            log.error("Échec de la mise à jour de l'image pour le cours '{}'.", title);
            throw new RuntimeException("Erreur lors de la mise à jour de l'image.");
        }

        course.setTitle(title);
        course.setPrice(price);
        course.setImagePath(imagePath);

        Course updatedCourse = courseRepository.save(course);
        log.info("Cours avec l'ID {} mis à jour avec succès.", course.getId());
        return updatedCourse;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        log.info("Récupération de tous les cours avec succès. Nombre de cours trouvés: {}", courses.size());
        return courses;
    }

    public Course getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        log.info("Cours avec l'ID {} récupéré avec succès.", id);
        return course;
    }


    public void deleteCourse(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.deleteById(id);
        log.info("Cours avec l'ID {} supprimé avec succès.", id);
    }



}
