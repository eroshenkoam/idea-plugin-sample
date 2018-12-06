package io.eroshenkoam.idea.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssue implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private JiraIssueFields fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JiraIssueFields getFields() {
        return fields;
    }

    public void setFields(JiraIssueFields fields) {
        this.fields = fields;
    }
}
