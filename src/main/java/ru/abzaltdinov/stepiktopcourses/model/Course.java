package ru.abzaltdinov.stepiktopcourses.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Course {
    private int id;
    private long learnersCount;
    private String title;
}
