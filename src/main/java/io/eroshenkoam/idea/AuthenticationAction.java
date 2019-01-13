package io.eroshenkoam.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.eroshenkoam.idea.ui.AuthenticationForm;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class AuthenticationAction extends AnAction {

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final AuthenticationForm form = new AuthenticationForm();
        form.setVisible(true);
    }

}
