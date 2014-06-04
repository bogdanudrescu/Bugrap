package com.example.bugrap;

import java.util.Locale;
import java.util.Set;

import org.vaadin.bugrap.domain.entities.Project;

import com.example.bugrap.data.DataManager;
import com.example.bugrap.data.LoginManager;
import com.example.bugrap.resources.BugrapResources;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The main view.
 * 
 * @author bogdan
 */
@SuppressWarnings("serial")
public class BugrapPage extends Panel implements View {

	/*
	 * The project view where the selected project should be set.
	 */
	private ProjectView projectView;

	/**
	 * Create the main page.
	 */
	public BugrapPage() {
		projectView = new ProjectView();
		projectView.setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		//		GridLayout layout = new GridLayout(1, 2);
		layout.setSizeFull();

		setContent(layout);

		Component titleView = createTitle();
		//		titleView.setHeight("28px"); // FIXME this is odd.

		//		layout.addComponent(titleView, 0, 0);
		//		layout.addComponent(projectView, 0, 1);
		//		layout.setRowExpandRatio(1, 1);

		layout.addComponent(titleView);
		layout.addComponent(projectView);

		layout.setExpandRatio(projectView, 1);
	}

	/*
	 * The title view.
	 */
	private Component createTitle() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");

		ComboBox projectsComboBox = new ComboBox();
		//projectsComboBox.setWidth("100%");
		projectsComboBox.addValueChangeListener(new ProjectChangeListener());

		//* // To bad I have to add them manually.
		Set<Project> projects = DataManager.getBugrapRepository().findProjects();
		System.out.println("projects: " + projects);

		Project defaultProject = null;

		for (Project project : projects) {
			projectsComboBox.addItem(project);

			if (defaultProject == null) {
				defaultProject = project;
			}
		}

		projectsComboBox.setValue(defaultProject);
		//*/

		/* // FIXME This is not working.
		JPAContainer<Project> projectsDataContainer = new JPAContainer<Project>(Project.class);
		projectsDataContainer.setEntityProvider(new MutableLocalEntityProvider<Project>(Project.class, DataManager.getBugrapRepository()
				.getEntityManager()));

		System.out.println("ids: " + projectsDataContainer.getItemIds());

		projectsComboBox.setContainerDataSource(projectsDataContainer);
		//projectsComboBox.setConverter(new ProjectConverter());
		//*/

		//UserView userView = new UserView();

		Button userButton = new Button(LoginManager.getManager().getUser().getName(), BugrapResources.getInstance().getResource("girl.png"));
		userButton.addClickListener(new UserButtonListener());

		Button logoutButton = new Button("Logout", BugrapResources.getInstance().getResource("exit.png"));
		logoutButton.addClickListener(new LogoutButtonListener());

		layout.addComponent(projectsComboBox);
		//layout.addComponent(userView);
		layout.addComponent(userButton);
		layout.addComponent(logoutButton);

		layout.setExpandRatio(projectsComboBox, 1);

		return layout;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	/*
	 * Handle the user settings action.
	 */
	private class UserButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			// TODO Auto-generated method stub

		}
	}

	/*
	 * Handle the logout action.
	 */
	private class LogoutButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			// TODO: Add a confirmation message.

			getUI().getNavigator().navigateTo("login");
		}
	}

	/*
	 * Handle the selected project change.
	 */
	private class ProjectChangeListener implements ValueChangeListener {

		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@Override
		public void valueChange(ValueChangeEvent event) {
			projectView.setProject((Project) event.getProperty().getValue());
		}

	}

	/*
	 * Project converter to String presentation.
	 */
	private class ProjectConverter implements Converter<Object, Object> {

		/* (non-Javadoc)
		 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
		 */
		@Override
		public Object convertToModel(Object value, Class<? extends Object> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return null;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
		 */
		@Override
		public Object convertToPresentation(Object value, Class<? extends Object> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {

			System.out.println("value: " + value); // This is null here

			if (value instanceof Project) {
				Project project = (Project) value;
				return project.getName();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.util.converter.Converter#getModelType()
		 */
		@Override
		public Class<Object> getModelType() {
			return Object.class;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.util.converter.Converter#getPresentationType()
		 */
		@Override
		public Class<Object> getPresentationType() {
			return Object.class;
		}

	}

}
