package io.eroshenkoam.idea.jira;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public interface JiraClient {

    @GET("api/2/search")
    JiraFilter findIssue(@Query(value = "jql", encoded = true) String jql);

    @GET("api/2/issue/{key}")
    JiraIssue getIssue(@Path("key") String key);

}
