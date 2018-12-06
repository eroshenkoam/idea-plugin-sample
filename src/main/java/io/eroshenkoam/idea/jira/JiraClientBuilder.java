package io.eroshenkoam.idea.jira;

import io.eroshenkoam.idea.retrofit.BasicAuthInterceptor;
import io.eroshenkoam.idea.retrofit.DefaultCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class JiraClientBuilder {

    public JiraClient build() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("admin", "admin"))
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new DefaultCallAdapterFactory<>())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://localhost:2990/jira/rest/")
                .client(client)
                .build();

        return retrofit.create(JiraClient.class);
    }
}
