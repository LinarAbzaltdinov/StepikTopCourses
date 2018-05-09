package ru.abzaltdinov.stepiktopcourses.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abzaltdinov.stepiktopcourses.model.Course;
import ru.abzaltdinov.stepiktopcourses.service.CoursesLoaderService;

import java.util.List;

@RestController
public class CoursesController {

    private final CoursesLoaderService coursesLoaderService;

    @Autowired
    public CoursesController(CoursesLoaderService coursesLoaderService) {
        this.coursesLoaderService = coursesLoaderService;
    }

    @GetMapping("/load")
    public ResponseEntity<List<Course>> loadTopCourses(@RequestParam("amount") int amount) {
        List<Course> result = coursesLoaderService.getTopCourses(amount);
        ResponseEntity<List<Course>> response;
        if (result == null) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<>(result, HttpStatus.OK);
        }
        return response;
    }
}
