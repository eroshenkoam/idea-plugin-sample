package io.eroshenkoam.idea.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import io.eroshenkoam.idea.jira.JiraClient;
import io.eroshenkoam.idea.jira.JiraClientBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class AuthenticationForm extends JDialog {

    private static final Logger LOGGER = new DefaultLogger(AuthenticationForm.class.getName());

    private static final String ENDPOINT_KEY = "eroshenkoam.jira.endpoint";
    private static final String USERNAME_KEY = "eroshenkoam.jira.username";
    private static final String PASSWORD_KEY = "eroshenkoam.jira.password";

    private JButton saveButton;
    private JButton testButton;
    private JButton cancelButton;
    private JPanel rootPanel;

    private JTextField endpointField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField statusField;

    public AuthenticationForm() {
        setTitle("Authentication Credentials");
        setSize(400, 200);
        add(rootPanel);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

        cancelButton.addActionListener(e -> handleCancelButton());
        testButton.addActionListener(e -> handleTestButton());
        saveButton.addActionListener(e -> handleOkButton());

        endpointField.setText(PropertiesComponent.getInstance().getValue(ENDPOINT_KEY));
        usernameField.setText(PropertiesComponent.getInstance().getValue(USERNAME_KEY));
        passwordField.setText(PropertiesComponent.getInstance().getValue(PASSWORD_KEY));

        statusField.setText("press 'Test' button");
    }

    private void handleCancelButton() {
        setVisible(false);
    }

    private void handleTestButton() {
        Optional<String> username = getJiraUsername();
        if (username.isPresent()) {
            statusField.setText(String.format("authorized as '%s'", username.get()));
        } else {
            statusField.setText("Bad credentials");
        }
    }

    private void handleOkButton() {
        Optional<String> username = getJiraUsername();
        if (username.isPresent()) {
            PropertiesComponent.getInstance().setValue(ENDPOINT_KEY, endpointField.getText());
            PropertiesComponent.getInstance().setValue(USERNAME_KEY, usernameField.getText());
            PropertiesComponent.getInstance().setValue(PASSWORD_KEY, String.valueOf(passwordField.getPassword()));
            setVisible(false);
        } else {
            statusField.setText("Bad credentials");
        }
    }

    private Optional<String> getJiraUsername() {
        if (isEmpty(endpointField) && isEmpty(usernameField) && isEmpty(passwordField)) {
            return Optional.empty();
        }
        final JiraClient client = getJiraClient();
        try {
            return Optional.of(client.getUser().getName());
        } catch (Exception e) {
            LOGGER.error(e);
            return Optional.empty();
        }
    }

    private boolean isEmpty(final JTextField field) {
        return StringUtils.isBlank(field.getText());
    }

    private boolean isEmpty(final JPasswordField field) {
        return StringUtils.isBlank(String.copyValueOf(field.getPassword()));
    }

    private JiraClient getJiraClient() {
        return new JiraClientBuilder()
                .endpoint(endpointField.getText())
                .username(usernameField.getText())
                .password(String.copyValueOf(passwordField.getPassword()))
                .build();
    }
}
