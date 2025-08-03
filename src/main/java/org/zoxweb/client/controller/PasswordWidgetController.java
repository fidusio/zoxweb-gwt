package org.zoxweb.client.controller;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class PasswordWidgetController {


    private final PasswordTextBox passwordTextBox;


    public PasswordWidgetController(PasswordTextBox passwordField, CheckBox showPassword) {
        this.passwordTextBox = passwordField;
        //this.showPassword = showPassword;

        // Listen for checkbox changes
        showPassword.addValueChangeHandler(event ->
                passwordTextBox.getElement()
                        .setAttribute("type", event.getValue() ? "text" : "password")
        );
    }

    public PasswordTextBox getWidget() {
        return passwordTextBox;
    }

    /**
     * Expose get/set for the password value
     */
    public String getContent() {
        return passwordTextBox.getText();
    }

    public void setContent(String pw) {
        passwordTextBox.setText(pw);
    }
}