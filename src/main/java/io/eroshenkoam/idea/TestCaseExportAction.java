package io.eroshenkoam.idea;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import io.eroshenkoam.idea.export.TestCase;
import io.eroshenkoam.idea.util.FreemarkerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class TestCaseExportAction extends BaseExportAction {

    @Override
    public void exportTestCases(final AnActionEvent event, final Map<PsiMethod, TestCase> testCasesMap) {
        final Project project = event.getProject();
        final Path exportPath = Paths.get(project.getBasePath()).resolve("index.html");
        try {
            Files.write(exportPath, FreemarkerUtils.processTemplate("testcases.ftl", testCasesMap.values()).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
