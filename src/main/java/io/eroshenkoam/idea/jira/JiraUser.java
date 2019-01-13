package io.eroshenkoam.idea.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraUser {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
