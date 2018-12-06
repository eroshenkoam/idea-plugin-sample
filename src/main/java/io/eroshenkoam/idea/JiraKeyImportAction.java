package io.eroshenkoam.idea;

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
import io.eroshenkoam.idea.jira.JiraIssue;
import io.eroshenkoam.idea.util.PsiUtils;

import java.util.Arrays;
import java.util.Optional;

import static io.eroshenkoam.idea.Annotations.ALLURE2_TMS_LINK_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_DISPLAY_NAME_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TEST_ANNOTATION;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class JiraKeyImportAction extends AnAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {
        final PsiElement element = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiClass) {
            final PsiClass psiClass = (PsiClass) element;
            Arrays.stream(psiClass.getMethods())
                    .filter(m -> m.hasAnnotation(JUNIT_DISPLAY_NAME_ANNOTATION))
                    .filter(m -> m.hasAnnotation(JUNIT_TEST_ANNOTATION))
                    .forEach(this::createTmsLinkAnnotation);
        }
    }

    private void createTmsLinkAnnotation(final PsiMethod method) {
        final String name = Optional.ofNullable(method.getAnnotation(JUNIT_DISPLAY_NAME_ANNOTATION))
                .map(a -> a.findDeclaredAttributeValue("value"))
                .map(PsiElement::getText)
                .get();

        final JiraClient jiraClient = createJiraClient();
        final String key = jiraClient.findIssue(String.format("text ~ %s", name)).getIssues().stream()
                .map(JiraIssue::getKey)
                .findFirst().get();

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

    private JiraClient createJiraClient() {
        return new JiraClientBuilder().build();
    }
}
