package ru.abzaltdinov.stepiktopcourses.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.abzaltdinov.stepiktopcourses.client.StepikAPIClient;
import ru.abzaltdinov.stepiktopcourses.model.Course;
import ru.abzaltdinov.stepiktopcourses.model.CoursesWithMeta;

import java.io.IOException;
import java.util.*;

@Service
public class CoursesLoaderService {

    private final StepikAPIClient stepikAPIClient;

    private Set<Course> allCourses = new HashSet<>();
    private ArrayList<Course> sortedCourses = new ArrayList<>();
    private Comparator<Course> courseComparator =
            Comparator.comparingLong(Course::getLearnersCount).reversed();

    @Autowired
    public CoursesLoaderService(StepikAPIClient stepikAPIClient) {
        this.stepikAPIClient = stepikAPIClient;
    }

    public List<Course> getTopCourses(int amount) {
        if (amount <= 0) {
            return null;
        }
        List<Course> newCourses = loadNewCourses();
        if (allCourses.isEmpty()) {
            sortedCourses.addAll(newCourses);
            sortedCourses.sort(courseComparator);
        } else {
            for (Course course : newCourses) {
                int indexToInsert = Collections.binarySearch(sortedCourses, course, courseComparator);
                if (indexToInsert < 0) {
                    indexToInsert = -indexToInsert - 1;
                }
                sortedCourses.add(indexToInsert, course);
            }
        }
        allCourses.addAll(newCourses);
        if (amount > sortedCourses.size()) {
            amount = sortedCourses.size();
        }
        return sortedCourses.subList(0, amount);
    }

    private List<Course> loadNewCourses() {
        List<Course> result = new ArrayList<>();
        int nextPage = 1;
        boolean hasNextPage = true;
        boolean loadedAlreadyExistedCourses = false;

        while (hasNextPage && !loadedAlreadyExistedCourses) {
            Response<CoursesWithMeta> coursesWithMetaResponse = null;
            try {
                coursesWithMetaResponse = stepikAPIClient.getCourses(nextPage).execute();
            } catch (IOException e) {
                break;
            }
            if (!coursesWithMetaResponse.isSuccessful()) {
                break;
            }
            CoursesWithMeta coursesWithMeta = coursesWithMetaResponse.body();
            for (Course course : coursesWithMeta.getCourses()) {
                if (allCourses.contains(course)) {
                    loadedAlreadyExistedCourses = true;
                    break;
                }
                result.add(course);
            }
            hasNextPage = coursesWithMeta.getMeta().isHasNext();
            nextPage++;
        }
        return result;
    }
}
