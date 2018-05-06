package ru.abzaltdinov.stepiktopcourses.model;

import lombok.Data;

import java.util.List;

@Data
public class CoursesWithMeta {
    MetaData meta;
    List<Course> courses;
}
