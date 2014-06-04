package com.example.bugrap;

import java.io.Serializable;

import com.example.bugrap.data.LoginManager;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * The login page.
 * 
 * @author bogdan
 */
@SuppressWarnings("serial")
public class LoginPage extends Panel implements View {

	/*
	 * The username.
	 */
	private Property<String> username;

	/*
	 * The password.
	 */
	private Property<String> password;

	/*
	 * The login delegate.
	 */
	private LoginDelegate loginDelegate;

	/*
	 * The username field.
	 */
	private TextField usernameField;

	/**
	 * Create the login page.
	 * @param loginDelegate	manage what happen during and after login.
	 */
	public LoginPage(LoginDelegate loginDelegate) {
		this.loginDelegate = loginDelegate;

		username = new ObjectProperty<String>(DEFAULT_USERNAME);
		password = new ObjectProperty<String>(DEFAULT_PASSWORD);

		usernameField = new TextField(username);
		usernameField.setCaption("Username");

		PasswordField passwordField = new PasswordField(password);
		passwordField.setCaption("Password");

		Button loginButton = new Button("Login");
		loginButton.setClickShortcut(KeyCode.ENTER);
		loginButton.addClickListener(new LoginButtonListener());

		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		vLayout.setSpacing(true);

		vLayout.addComponent(usernameField);
		vLayout.addComponent(passwordField);
		vLayout.addComponent(loginButton);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		Label topLabel = new Label("&nbsp;", ContentMode.HTML);
		topLabel.setSizeFull();
		Label bottomLabel = new Label("&nbsp;", ContentMode.HTML);
		bottomLabel.setSizeFull();

		layout.addComponent(topLabel);
		layout.addComponent(vLayout);
		layout.addComponent(bottomLabel);

		layout.setComponentAlignment(topLabel, Alignment.TOP_CENTER);
		layout.setComponentAlignment(bottomLabel, Alignment.BOTTOM_CENTER);

		setContent(layout);

	}

	/*
	 * Click listener for the login button.
	 */
	private class LoginButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {

			if (LoginManager.getManager().login(username.getValue(), password.getValue())) {
				loginDelegate.showContent();
			} else {
				Notification.show("Login failed!");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		username.setValue(DEFAULT_USERNAME);
		password.setValue(DEFAULT_PASSWORD);

		usernameField.focus();
	}

	/*
	 * Test defaults.
	 */
	private static final String DEFAULT_USERNAME = "manager";
	private static final String DEFAULT_PASSWORD = "manager";

	/**
	 * Delegate content creation and other stuff.
	 * 
	 * @author bogdan
	 */
	public interface LoginDelegate extends Serializable {

		/**
		 * Show the app content.
		 */
		void showContent();

	}

}
