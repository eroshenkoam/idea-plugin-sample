package io.eroshenkoam.idea;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.lang.jvm.JvmNamedElement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.eroshenkoam.idea.util.PsiUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.eroshenkoam.idea.Annotations.ALLURE1_FEATURES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE1_STEP_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE1_STORIES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE1_TESTCASEID_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_FEATURES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_FEATURE_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_STEP_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_STORIES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_STORY_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_TMS_LINK_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TEST_ANNOTATION;
import static io.eroshenkoam.idea.util.PsiUtils.createAnnotation;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class AllureMigrationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final PsiElement element = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiClass) {
            Arrays.stream(((PsiClass) element).getMethods())
                    .filter(m -> m.hasAnnotation(JUNIT_TEST_ANNOTATION))
                    .forEach(this::migrateTestAnnotations);

            Arrays.stream(((PsiClass) element).getMethods())
                    .filter(m -> m.hasAnnotation(ALLURE1_STEP_ANNOTATION))
                    .forEach(this::migrateStepAnnotation);
        }
    }

    private void migrateTestAnnotations(final PsiMethod testMethod) {
        migrateTestCaseId(testMethod);
        migrateFeaturesAnnotation(testMethod);
        migrateStoriesAnnotation(testMethod);
        migrateStepAnnotation(testMethod);
    }

    private void migrateTestCaseId(final PsiMethod testMethod) {
        Optional.ofNullable(testMethod.getAnnotation(ALLURE1_TESTCASEID_ANNOTATION)).ifPresent(testCaseId -> {
            final String id = testCaseId.findAttributeValue("value").getText();

            final String tmsLinkText = String.format("@%s(%s)", ALLURE2_TMS_LINK_ANNOTATION, id);
            final PsiAnnotation tmsLink = createAnnotation(tmsLinkText, testMethod);
            final Project project = testMethod.getProject();
            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_TMS_LINK_ANNOTATION);

                testMethod.getModifierList().addAfter(tmsLink, testCaseId);
                testCaseId.delete();

                PsiUtils.optimizeImports(testMethod.getContainingFile());
            }), "Migrate Allure TestCaseId", null);
        });
    }

    private void migrateFeaturesAnnotation(final PsiMethod testMethod) {
        Optional.ofNullable(testMethod.getAnnotation(ALLURE1_FEATURES_ANNOTATION)).ifPresent(oldFeaturesAnnotation -> {
            final PsiArrayInitializerMemberValue value = (PsiArrayInitializerMemberValue) oldFeaturesAnnotation
                    .findDeclaredAttributeValue("value");

            final List<String> features = Arrays.stream(value.getInitializers())
                    .map(PsiAnnotationMemberValue::getText)
                    .collect(Collectors.toList());

            final String featuresText = getFeaturesAnnotationText(features);
            final PsiAnnotation featureAnnotation = createAnnotation(featuresText, testMethod);

            final Project project = testMethod.getProject();
            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_FEATURE_ANNOTATION);
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_FEATURES_ANNOTATION);

                testMethod.getModifierList().addAfter(featureAnnotation, oldFeaturesAnnotation);
                oldFeaturesAnnotation.delete();

                PsiUtils.optimizeImports(testMethod.getContainingFile());
            }), "Migrate Allure Features", null);
        });
    }

    private void migrateStoriesAnnotation(final PsiMethod testMethod) {
        Optional.ofNullable(testMethod.getAnnotation(ALLURE1_STORIES_ANNOTATION)).ifPresent(oldStoriesAnnotation -> {
            final PsiArrayInitializerMemberValue value = (PsiArrayInitializerMemberValue) oldStoriesAnnotation
                    .findDeclaredAttributeValue("value");

            final List<String> stories = Arrays.stream(value.getInitializers())
                    .map(PsiAnnotationMemberValue::getText)
                    .collect(Collectors.toList());

            final String storiesText = getStoriesAnnotationText(stories);
            final PsiAnnotation storiesAnnotation = createAnnotation(storiesText, testMethod);

            final Project project = testMethod.getProject();
            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_STORY_ANNOTATION);
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_STORIES_ANNOTATION);

                testMethod.getModifierList().addAfter(storiesAnnotation, oldStoriesAnnotation);
                oldStoriesAnnotation.delete();

                PsiUtils.optimizeImports(testMethod.getContainingFile());
            }), "Migrate Allure Stories", null);
        });
    }

    private void migrateStepAnnotation(final PsiMethod testMethod) {
        Optional.ofNullable(testMethod.getAnnotation(ALLURE1_STEP_ANNOTATION)).ifPresent(oldStepAnnotation -> {
            final String oldStepValue = AnnotationUtil.getDeclaredStringAttributeValue(oldStepAnnotation, "value");

            final String[] params = Arrays.stream(testMethod.getParameters())
                    .map(JvmNamedElement::getName)
                    .toArray(String[]::new);

            final String stepValue = convert(oldStepValue, params);
            final String stepText = String.format("@%s(\"%s\")", ALLURE2_STEP_ANNOTATION, stepValue);

            final PsiAnnotation stepAnnotation = createAnnotation(stepText, testMethod);

            final Project project = testMethod.getProject();
            CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                PsiUtils.addImport(testMethod.getContainingFile(), ALLURE2_STEP_ANNOTATION);
                testMethod.getModifierList().addAfter(stepAnnotation, oldStepAnnotation);
                oldStepAnnotation.delete();
                PsiUtils.optimizeImports(testMethod.getContainingFile());
            }), "Migrate Allure Steps", null);
        });
    }

    private String getFeaturesAnnotationText(final List<String> features) {
        final String body = features.stream()
                .map(label -> String.format("@%s(%s)", ALLURE2_FEATURE_ANNOTATION, label))
                .collect(Collectors.joining(","));
        return String.format("@%s({%s})", ALLURE2_FEATURES_ANNOTATION, body);
    }

    private String getStoriesAnnotationText(final List<String> features) {
        final String body = features.stream()
                .map(label -> String.format("@%s(%s)", ALLURE2_STORY_ANNOTATION, label))
                .collect(Collectors.joining(","));
        return String.format("@%s({%s})", ALLURE2_STORIES_ANNOTATION, body);
    }

    private String convert(final String stepValue, final String[] params) {
        String result = stepValue;
        for (int i = 0; i < params.length; i++) {
            result = result.replace("{" + i + "}", "{" + params[i] + "}");
        }
        return result;
    }
}
