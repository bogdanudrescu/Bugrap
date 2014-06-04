package com.example.bugrap.data;

import java.util.Set;

import org.vaadin.bugrap.domain.PasswordGenerator;
import org.vaadin.bugrap.domain.entities.Reporter;

/**
 * Manage the login stuff.
 * 
 * @author bogdan
 */
public class LoginManager {

	/*
	 * The single instance.
	 */
	private static LoginManager manager = new LoginManager();

	/**
	 * Gets the single instance of login manager.
	 * @return	the single instance of login manager.
	 */
	public static LoginManager getManager() {
		return manager;
	}

	/*
	 * The current logged in user.
	 */
	private Reporter user;

	/**
	 * Check the specified login.
	 * @param username	the username.
	 * @param password	the password.
	 * @return	true if the credentials are correct, otherwise false.
	 */
	public boolean login(String username, String password) {
		if (username == null || password == null) {
			return false;
		}

		// Encrypt the pass.
		String encryptPassword;
		try {
			encryptPassword = PasswordGenerator.encrypt(password);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		// Search for the user.
		Set<Reporter> reporters = DataManager.getBugrapRepository().findReporters();
		for (Reporter reporter : reporters) {

			if (username.equals(reporter.getName())) {

				if (encryptPassword.equals(reporter.getPassword())) {
					setUser(reporter);

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Logout the current user.
	 */
	public void logout() {
		setUser(null);
	}

	/*
	 * Sets the current user.
	 */
	private synchronized void setUser(Reporter user) {
		this.user = user;
	}

	/**
	 * Gets the current logged in user.
	 * @return	the current logged in user.
	 */
	public Reporter getUser() {
		return user;
	}

}
