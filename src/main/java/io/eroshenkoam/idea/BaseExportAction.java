package io.eroshenkoam.idea;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiStatement;
import io.eroshenkoam.idea.export.TestCase;
import io.eroshenkoam.idea.export.TestStep;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.eroshenkoam.idea.Annotations.ALLURE2_FEATURES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_STEP_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_STORIES_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.ALLURE2_TMS_LINK_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_DISPLAY_NAME_ANNOTATION;
import static io.eroshenkoam.idea.Annotations.JUNIT_TEST_ANNOTATION;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public abstract class BaseExportAction extends AnAction {

    public abstract void exportTestCases(final AnActionEvent event, final Map<PsiMethod, TestCase> testCaseMap);

    @Override
    public void actionPerformed(final AnActionEvent event) {
        final PsiElement element = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiClass) {
            final PsiClass psiClass = (PsiClass) element;
            final Map<PsiMethod, TestCase> testCaseMap = Arrays.stream(psiClass.getMethods())
                    .filter(m -> m.hasAnnotation(JUNIT_TEST_ANNOTATION))
                    .collect(Collectors.toMap(m -> m, this::exportTestCaseFromMethod));
            exportTestCases(event, testCaseMap);
        }
    }

    private TestCase exportTestCaseFromMethod(final PsiMethod method) {
        final TestCase testCase = new TestCase();
        getId(method).ifPresent(testCase::setId);
        testCase.setName(getName(method));
        testCase.setFeatures(getValues(method, ALLURE2_FEATURES_ANNOTATION));
        testCase.setStories(getValues(method, ALLURE2_STORIES_ANNOTATION));
        testCase.setSteps(getSteps(method));
        return testCase;
    }

    private static Optional<String> getId(final PsiMethod method) {
        return Optional.ofNullable(method.getAnnotation(ALLURE2_TMS_LINK_ANNOTATION))
                .map(a -> AnnotationUtil.getStringAttributeValue(a, "value"));
    }

    private static String getName(final PsiMethod method) {
        return AnnotationUtil.getStringAttributeValue(method.getAnnotation(JUNIT_DISPLAY_NAME_ANNOTATION), "value");
    }

    private static List<TestStep> getSteps(final PsiMethod method) {
        final PsiStatement[] statements = Optional.ofNullable(method.getBody())
                .map(PsiCodeBlock::getStatements)
                .orElse(new PsiStatement[]{});

        final List<PsiMethodCallExpression> methodCallExpressions = Arrays.stream(statements)
                .map(PsiElement::getChildren)
                .flatMap(Arrays::stream)
                .filter(PsiMethodCallExpression.class::isInstance)
                .map(PsiMethodCallExpression.class::cast)
                .collect(Collectors.toList());

        return methodCallExpressions.stream()
                .map(PsiMethodCallExpression::resolveMethod)
                .filter(Objects::nonNull)
                .filter(BaseExportAction::isStepMethod)
                .map(m -> m.getAnnotation(ALLURE2_STEP_ANNOTATION))
                .map(s -> AnnotationUtil.getStringAttributeValue(s, "value"))
                .map(s -> new TestStep().setName(s))
                .collect(Collectors.toList());

    }

    private List<String> getValues(final PsiMethod method, final String annotationText) {
        final PsiAnnotation annotation = method.getAnnotation(annotationText);
        final PsiArrayInitializerMemberValue value = (PsiArrayInitializerMemberValue) annotation
                .findDeclaredAttributeValue("value");
        return Arrays.stream(value.getInitializers())
                .map(PsiAnnotation.class::cast)
                .map(a -> a.findDeclaredAttributeValue("value"))
                .map(PsiAnnotationMemberValue::getText)
                .map(text -> text.substring(1, text.length() -1))
                .collect(Collectors.toList());
    }

    private static boolean isStepMethod(final PsiMethod method) {
        return method.hasAnnotation(ALLURE2_STEP_ANNOTATION);
    }

}
