package ru.abzaltdinov.stepiktopcourses.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import ru.abzaltdinov.stepiktopcourses.client.StepikAPIClient;
import ru.abzaltdinov.stepiktopcourses.model.Course;
import ru.abzaltdinov.stepiktopcourses.model.CoursesWithMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class CoursesLoaderService {

    private final StepikAPIClient stepikAPIClient;
    private final Logger logger = LoggerFactory.getLogger(CoursesLoaderService.class);

    private Comparator<Course> courseComparator =
            Comparator.comparingLong(Course::getLearnersCount).reversed();

    private TreeSet<Course> allSortedCourses = new TreeSet<>(courseComparator);


    @Autowired
    public CoursesLoaderService(StepikAPIClient stepikAPIClient) {
        this.stepikAPIClient = stepikAPIClient;
    }

    public List<Course> getTopCourses(int amount) {
        if (amount <= 0) {
            return null;
        }

        List<Course> newCourses = loadNewCourses();

        if (newCourses == null) {
            return null;
        }
        allSortedCourses.addAll(newCourses);
        if (amount > allSortedCourses.size()) {
            amount = allSortedCourses.size();
        }
        ArrayList<Course> resultedTopCourses = new ArrayList<>(amount);
        int currAmount = 0;
        for (Course course : allSortedCourses) {
            resultedTopCourses.add(course);
            currAmount++;
            if (currAmount == amount) {
                break;
            }
        }
        return resultedTopCourses;
    }

    private List<Course> loadNewCourses() {
        int coresAmount = Runtime.getRuntime().availableProcessors();
        List<Callable<List<Course>>> loadCoursesTasks = new ArrayList<>(coresAmount);
        for (int i = 0; i < coresAmount; ++i) {
            final int threadNumber = i + 1;
            loadCoursesTasks.add(() -> {
                List<Course> result = new ArrayList<>();
                int pageNumber = threadNumber;
                boolean loadedAlreadyExistingCourses = false;
                while (!loadedAlreadyExistingCourses) {
                    Response<CoursesWithMeta> coursesWithMetaResponse;
                    try {
                        coursesWithMetaResponse = stepikAPIClient.getCourses(pageNumber).execute();
                    } catch (IOException e) {
                        break;
                    }
                    if (!coursesWithMetaResponse.isSuccessful() || coursesWithMetaResponse.body() == null) {
                        break;
                    }
                    logger.debug("PAGE " + pageNumber + " of Stepik API loaded.");
                    CoursesWithMeta coursesWithMeta = coursesWithMetaResponse.body();
                    for (Course course : coursesWithMeta.getCourses()) {
                        if (allSortedCourses.contains(course)) {
                            loadedAlreadyExistingCourses = true;
                            break;
                        }
                        result.add(course);
                    }
                    pageNumber += coresAmount;
                }
                return result;
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(coresAmount);
        List<Course> newLoadedCourses = null;
        try {
            newLoadedCourses = executorService.invokeAll(loadCoursesTasks)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            logger.debug(e.toString());
        }
        return newLoadedCourses;
    }
}
