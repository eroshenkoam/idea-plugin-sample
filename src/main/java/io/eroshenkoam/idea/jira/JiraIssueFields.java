package io.eroshenkoam.idea.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueFields {

    private List<String> labels;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
