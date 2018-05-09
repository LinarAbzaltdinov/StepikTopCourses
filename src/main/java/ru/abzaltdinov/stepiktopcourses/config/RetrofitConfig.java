package ru.abzaltdinov.stepiktopcourses.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.abzaltdinov.stepiktopcourses.client.StepikAPIClient;

@Configuration
public class RetrofitConfig {

    private final String STEPIK_API_HOST = "https://stepik.org/api/";

    @Bean
    public StepikAPIClient stepikAPIClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(STEPIK_API_HOST)
                .build();
        return retrofit.create(StepikAPIClient.class);
    }
}
