package io.eroshenkoam.idea.jira;

import io.eroshenkoam.idea.retrofit.BasicAuthInterceptor;
import io.eroshenkoam.idea.retrofit.DefaultCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class JiraClientBuilder {

    private String endpoint;

    private String username;

    private String password;

    public JiraClientBuilder endpoint(String endpoint) {
        Objects.requireNonNull(endpoint);
        this.endpoint = endpoint;
        return this;
    }

    public JiraClientBuilder username(String username) {
        Objects.requireNonNull(username);
        this.username = username;
        return this;
    }

    public JiraClientBuilder password(String password) {
        Objects.requireNonNull(password);
        this.password = password;
        return this;
    }

    public JiraClient build() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(username, password))
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new DefaultCallAdapterFactory<>())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(endpoint)
                .client(client)
                .build();

        return retrofit.create(JiraClient.class);
    }
}
