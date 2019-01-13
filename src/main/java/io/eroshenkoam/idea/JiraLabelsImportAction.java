package io.eroshenkoam.idea;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.eroshenkoam.idea.jira.JiraClient;
import io.eroshenkoam.idea.jira.JiraClientBuilder;
import io.eroshenkoam.idea.util.PsiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.eroshenkoam.idea.Annotations.ALLURE2_TMS_LINK_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TAGS_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TAG_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TEST_ANNOTATION;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class JiraLabelsImportAction extends AnAction {


    @Override
    public void actionPerformed(final AnActionEvent event) {
        final PsiElement element = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiClass) {
            final PsiClass psiClass = (PsiClass) element;
            Arrays.stream(psiClass.getMethods())
                    .filter(m -> m.hasAnnotation(ALLURE2_TMS_LINK_ANNOTATION))
                    .filter(m -> m.hasAnnotation(JUNIT_TEST_ANNOTATION))
                    .forEach(this::createTagsAnnotation);
        }
    }

    private void createTagsAnnotation(final PsiMethod method) {
        final String issueKey = AnnotationUtil
                .getStringAttributeValue(method.getAnnotation(ALLURE2_TMS_LINK_ANNOTATION), "value");

        final JiraClient jiraClient = createJiraClient();
        Optional.ofNullable(jiraClient.getIssue(issueKey)).ifPresent(issue -> {
            final List<String> tags = Optional.ofNullable(issue.getFields().getLabels())
                    .orElseGet(ArrayList::new);

            final String tagsAnnotationText = getTagsAnnotationText(tags);
            final PsiAnnotation tagsAnnotation = PsiUtils.createAnnotation(tagsAnnotationText, method);

            final Project project = method.getProject();
            final PsiAnnotation tmsLinkAnnotation = method.getAnnotation(ALLURE2_TMS_LINK_ANNOTATION);

            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                Optional.ofNullable(method.getAnnotation(JUNIT_TAGS_ANNOTATION)).ifPresent(PsiAnnotation::delete);
                Optional.ofNullable(method.getAnnotation(JUNIT_TAG_ANNOTATION)).ifPresent(PsiAnnotation::delete);

                PsiUtils.addImport(method.getContainingFile(), JUNIT_TAGS_ANNOTATION);
                PsiUtils.addImport(method.getContainingFile(), JUNIT_TAG_ANNOTATION);

                method.getModifierList().addAfter(tagsAnnotation, tmsLinkAnnotation);
                PsiUtils.optimizeImports(method.getContainingFile());
            }), "Insert Jira labels", null);
        });

    }

    private String getTagsAnnotationText(final List<String> labels) {
        final String body = labels.stream()
                .map(label -> String.format("@%s(\"%s\")", JUNIT_TAG_ANNOTATION, label))
                .collect(Collectors.joining(","));
        return String.format("@%s({%s})", JUNIT_TAGS_ANNOTATION, body);
    }

    private JiraClient createJiraClient() {
        return new JiraClientBuilder()
                .endpoint("http://localhost:2990/jira/rest/")
                .username("admin")
                .password("admin")
                .build();
    }

}
