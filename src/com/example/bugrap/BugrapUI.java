package com.example.bugrap;

import javax.servlet.annotation.WebServlet;

import org.vaadin.bugrap.domain.BugrapRepository;

import com.example.bugrap.LoginPage.LoginDelegate;
import com.example.bugrap.data.DataManager;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
@Theme("bugrap")
@Push(PushMode.MANUAL)
public class BugrapUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = BugrapUI.class, widgetset = "com.example.bugrap.widgetset.BugrapWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	/*
	 * The data repository.
	 */
	private BugrapRepository repository;

	/*
	 * The pages navigator.
	 */
	private Navigator navigator;

	/* (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {
		System.out.println("init " + this);

		navigator = new Navigator(this, this);
		repository = new BugrapRepository();

		DataManager.setBugrapRepository(repository);

		navigator.addViewChangeListener(new BugrapViewChangeListener());

		LoginPage loginPage = new LoginPage(new BugrapLoginDelegate());
		loginPage.setSizeFull();

		navigator.addView("login", loginPage);
		navigator.navigateTo("login");
	}

	/*
	 * Login delegate implementation for Bugrap.
	 */
	private class BugrapLoginDelegate implements LoginDelegate {

		/*
		 * The main content.
		 */
		private BugrapPage bugrapPage;

		/* (non-Javadoc)
		 * @see com.example.bugrap.LoginPage.LoginDelegate#showContent()
		 */
		@Override
		public void showContent() {
			if (bugrapPage == null) {
				bugrapPage = new BugrapPage();
				bugrapPage.setSizeFull();

				navigator.addView("bugrap", bugrapPage);
			}

			getUI().getNavigator().navigateTo("bugrap");
		}

	}

	/*
	 * Listen to view changes in the navigator.
	 */
	private class BugrapViewChangeListener implements ViewChangeListener {

		@Override
		public boolean beforeViewChange(ViewChangeEvent event) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void afterViewChange(ViewChangeEvent event) {
			// TODO Auto-generated method stub

		}
	}

}