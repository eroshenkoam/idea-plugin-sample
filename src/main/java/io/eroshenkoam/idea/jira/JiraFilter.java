package io.eroshenkoam.idea.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<JiraIssue> issues;

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }

}
