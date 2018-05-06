package ru.abzaltdinov.stepiktopcourses.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.abzaltdinov.stepiktopcourses.model.CoursesWithMeta;

public interface StepikAPIClient {

    @GET("courses")
    Call<CoursesWithMeta> getCourses();

    @GET("courses")
    Call<CoursesWithMeta> getCourses(@Query("page") int pageNumber);
}
