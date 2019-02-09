package io.eroshenkoam.idea.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueFields {

    private String summary;
    private String description;

    private JiraProject project;
    private JiraIssuetype issuetype;

    private List<String> labels;

    @JsonProperty("customfield_10002")
    private List<String> features;

    @JsonProperty("customfield_10003")
    private List<String> stories;

}
