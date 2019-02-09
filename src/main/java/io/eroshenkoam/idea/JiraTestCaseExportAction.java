package io.eroshenkoam.idea;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import io.eroshenkoam.idea.export.TestCase;
import io.eroshenkoam.idea.export.TestStep;
import io.eroshenkoam.idea.jira.JiraClient;
import io.eroshenkoam.idea.jira.JiraClientBuilder;
import io.eroshenkoam.idea.jira.JiraIssue;
import io.eroshenkoam.idea.jira.JiraIssueFields;
import io.eroshenkoam.idea.jira.JiraIssuetype;
import io.eroshenkoam.idea.jira.JiraProject;
import io.eroshenkoam.idea.util.PsiUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.eroshenkoam.idea.Annotations.ALLURE2_TMS_LINK_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TEST_ANNOTATION;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class JiraTestCaseExportAction extends BaseExportAction {

    @Override
    public void exportTestCases(final AnActionEvent event, final Map<PsiMethod, TestCase> testCasesMap) {
        final JiraClient client = createJiraClient();

        testCasesMap.forEach((method, testCase) -> {
            final JiraIssueFields fields = new JiraIssueFields()
                    .setSummary(testCase.getName())
                    .setDescription(toScenario(testCase.getSteps()))
                    .setProject(new JiraProject().setKey("NEW"))
                    .setIssuetype(new JiraIssuetype().setName("Задача"))
                    .setFeatures(toLabels(testCase.getFeatures()))
                    .setStories(toLabels(testCase.getStories()));
            final JiraIssue issue = client.createIssue(new JiraIssue().setFields(fields));
            createTmsLink(method, issue.getKey());
        });
    }

    private void createTmsLink(final PsiMethod method, String key) {
        final Project project = method.getProject();
        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
            PsiUtils.addImport(method.getContainingFile(), ALLURE2_TMS_LINK_ANNOTATION);

            Optional.ofNullable(method.getAnnotation(ALLURE2_TMS_LINK_ANNOTATION)).ifPresent(PsiAnnotation::delete);

            PsiAnnotation tmsLinks = createTmsIssueAnnotation(method, key);
            method.getModifierList().addAfter(tmsLinks, method.getAnnotation(JUNIT_TEST_ANNOTATION));

            PsiUtils.optimizeImports(method.getContainingFile());
        }), "Import Jira keys", null);
    }

    private PsiAnnotation createTmsIssueAnnotation(final PsiMethod method, final String key) {
        return PsiUtils.createAnnotation(String.format("@%s(\"%s\")", ALLURE2_TMS_LINK_ANNOTATION, key), method);
    }

    private String toScenario(final List<TestStep> steps) {
        steps.forEach(step -> System.out.println(step.getName()));
        return steps.stream()
                .map(TestStep::getName)
                .map(step -> step.replace("{", "\\{"))
                .map(step -> "# " + step)
                .collect(Collectors.joining("\r\n"));
    }

    private List<String> toLabels(final List<String> values) {
        return values.stream()
                .map(value -> value.replace(" ", "_"))
                .collect(Collectors.toList());
    }

    private JiraClient createJiraClient() {
        return new JiraClientBuilder()
                .endpoint("http://localhost:2990/jira/rest/")
                .username("admin")
                .password("admin")
                .build();
    }

}
