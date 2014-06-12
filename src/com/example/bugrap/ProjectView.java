package com.example.bugrap;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionEvent;
import org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionListener;
import org.vaadin.addons.buttongroup.HorizontalButtonGroupLayout;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.hene.popupbutton.PopupButton;

import com.example.bugrap.data.DataManager;
import com.example.bugrap.data.LoginManager;
import com.example.bugrap.report.ReportEditor;
import com.example.bugrap.report.ReportTable;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * View a single project details, including versions, reports, add new stuff to the project, etc.
 * 
 * @author bogdan
 */
@SuppressWarnings("serial")
public class ProjectView extends Panel {

	/*
	 * The search criteria.
	 */
	private SearchCriteria searchCriteria = new SearchCriteria();

	/*
	 * The container with the list of versions.
	 */
	private Container versionsDataSource = new BeanItemContainer<ProjectVersion>(ProjectVersion.class);

	/*
	 * The table containing reports.
	 */
	private ReportTable reportTable;

	/*
	 * The report editor.
	 */
	private ReportEditor reportEditor;

	/**
	 * Create the project view.
	 */
	public ProjectView() {

		// Buttons to create new issues or manage stuff.
		Button reportBugButton = new Button("Report a bug");
		Button requestFeatureButton = new Button("Request a feature");
		Button manageProjectButton = new Button("Manage project");

		// Search fields
		TextField searchField = new TextField(searchCriteria.freeText);

		ComboBox versionField = new ComboBox("Reports for", versionsDataSource);
		//		versionField.setNullSelectionAllowed(true);
		//		versionField.setNullSelectionItemId("All versions");
		versionField.setPropertyDataSource(searchCriteria.version);
		versionField.addValueChangeListener(new VersionChangeListener());

		// Button groups.
		HorizontalButtonGroupLayout assigneesGroup = new HorizontalButtonGroupLayout("Only me", "Everyone");
		assigneesGroup.setCaption("Assignees");
		assigneesGroup.getButtonGroup().addSelectionListener(new AssigneeChangeListener());

		HorizontalButtonGroupLayout statusGroup = new HorizontalButtonGroupLayout("Open", "All kinds");
		statusGroup.setCaption("Status");
		statusGroup.getButtonGroup().addSelectionListener(getStatusChangeListener());

		PopupButton statusCustom = new PopupButton("Custom");
		statusCustom.setContent(createStatusCustomPopup());
		statusGroup.getButtonGroup().addButton(statusCustom);

		// Define the layouts.
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);

		// First row with add report buttons.
		HorizontalLayout row0Layout = new HorizontalLayout();
		row0Layout.addComponent(reportBugButton);
		row0Layout.addComponent(requestFeatureButton);
		row0Layout.addComponent(manageProjectButton);
		row0Layout.addComponent(searchField);
		layout.addComponent(row0Layout);

		// Second row with the version selector.
		HorizontalLayout row1Layout = new HorizontalLayout();
		row1Layout.addComponent(versionField);
		layout.addComponent(row1Layout);

		// Third row with the assigned person and status.
		HorizontalLayout row2Layout = new HorizontalLayout();
		row2Layout.addComponent(assigneesGroup);
		row2Layout.addComponent(statusGroup);
		layout.addComponent(row2Layout);

		// Table with reports and editor.
		reportTable = new ReportTable();
		reportTable.addValueChangeListener(new ReportTableSelectionListener());
		reportEditor = new ReportEditor();
		reportEditor.getCommitObservable().addObserver(new ReportEditorCommitObserver());
		reportEditor.setBreadcrumbsVisible(false);

		VerticalSplitPanel splitPanel = new VerticalSplitPanel(reportTable, reportEditor);

		layout.addComponent(splitPanel);
		layout.setExpandRatio(splitPanel, 1);
	}

	/**
	 * Gets the current project.
	 * @return the project.
	 */
	public Project getProject() {
		return searchCriteria.getProject();
	}

	/**
	 * Sets the current project.
	 * @param project the project to set.
	 */
	public void setProject(Project project) {
		searchCriteria.setProject(project);

		// Sets the versions.
		versionsDataSource.removeAllItems();

		ProjectVersion projectVersionToSet = null;

		Set<ProjectVersion> projectVersions = DataManager.getBugrapRepository().findProjectVersions(project);
		for (ProjectVersion projectVersion : projectVersions) {
			versionsDataSource.addItem(projectVersion);

			if (projectVersionToSet == null) {
				projectVersionToSet = projectVersion;
			}
		}

		// Select a version.
		/*
		if (projectVersionToSet != null) {
			setProjectVersion(projectVersionToSet);
		}
		//*/

		//*
		setProjectVersion(null);
		//*/
	}

	/**
	 * Gets the project version.
	 * @return the projectVersion.
	 */
	public ProjectVersion getProjectVersion() {
		return searchCriteria.version.getValue();
	}

	/**
	 * Sets the new project version. If the version do not belong to the selected project and exception will rise.
	 * @param projectVersion the projectVersion to set.
	 */
	public void setProjectVersion(ProjectVersion projectVersion) {
		if (projectVersion != null && !searchCriteria.getProject().equals(projectVersion.getProject())) {
			throw new IllegalArgumentException("The version do not belong to the selected project.");
		}

		searchCriteria.version.setValue(projectVersion);

	}

	/*
	 * The status option group.
	 */
	private OptionGroup statusOptionGroup;

	/*
	 * The status change listener.
	 */
	private StatusChangeListener statusChangeListener;

	/*
	 * Creates the status change listener.
	 */
	private StatusChangeListener getStatusChangeListener() {
		if (statusChangeListener == null) {
			statusChangeListener = new StatusChangeListener();
		}
		return statusChangeListener;
	}

	/*
	 * Create the status custom popup with all the options.
	 */
	private Component createStatusCustomPopup() {
		statusOptionGroup = new OptionGroup();
		statusOptionGroup.setMultiSelect(true);
		statusOptionGroup.addValueChangeListener(getStatusChangeListener());

		for (Status status : Status.values()) {
			statusOptionGroup.addItem(status);
		}

		return statusOptionGroup;
	}

	/*
	 * Update the UI from the search status criteria.
	 * 
	 * Normally this should be a listener implementation, but we're in the same class and leak of time so no real reason to design this as it should.
	 */
	private void statusChanged() {
		System.out.println("statusChanged: " + searchCriteria.getSelectedStatus());

		statusOptionGroup.setValue(searchCriteria.getSelectedStatus());
	}

	/*
	 * Refresh the reports.
	 */
	private void refreshReports() {
		reportTable.refreshReports(searchCriteria.getQuery());
	}

	/*
	 * Listen to the changes of the version field.
	 */
	private class VersionChangeListener implements ValueChangeListener {

		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@Override
		public void valueChange(ValueChangeEvent event) {
			reportTable.setVersionColumnVisible(event.getProperty().getValue() == null);

			refreshReports();
		}

	}

	/*
	 * Handle the assignee selection.
	 */
	private class AssigneeChangeListener implements ButtonGroupSelectionListener {

		/* (non-Javadoc)
		 * @see org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionListener#selectedButtonChanged(org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionEvent)
		 */
		@Override
		public void selectedButtonChanged(ButtonGroupSelectionEvent event) {
			switch (event.getSelectedButtonIndex()) {
				case 0:
					searchCriteria.setAssignee(LoginManager.getManager().getUser());
					break;

				case 1:
					searchCriteria.setAssignee(null);
					break;
			}

			refreshReports();
		}

	}

	/*
	 * Listen when the status changes for the search criteria.
	 */
	private class StatusChangeListener implements ButtonGroupSelectionListener, ValueChangeListener {

		/* (non-Javadoc)
		 * @see org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionListener#selectedButtonChanged(org.vaadin.addons.buttongroup.ButtonGroup.ButtonGroupSelectionEvent)
		 */
		@Override
		public void selectedButtonChanged(ButtonGroupSelectionEvent event) {
			// Called from the ToggleButtonGroup with all customizable statuses

			switch (event.getSelectedButtonIndex()) {
				case 0:
					searchCriteria.setStatusOpen();
					refreshReports();
					break;

				case 1:
					searchCriteria.setStatusAll();
					refreshReports();
					break;
			}

		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void valueChange(ValueChangeEvent event) {
			// Called from the OptionGroup with all customizable statuses.
			searchCriteria.setSelectedStatus((Collection<Status>) event.getProperty().getValue());
			refreshReports();
		}
	}

	/*
	 * Search criteria.
	 * 
	 * FIXME: I don't really like how this is implemented. It mixes Property biding with setters.
	 */
	private class SearchCriteria implements Serializable, ValueChangeListener {

		/*
		 * The free text search field.
		 */
		final Property<String> freeText = new ObjectProperty<String>("", String.class);

		/*
		 * The version for which to display reports.
		 */
		final ObjectProperty<ProjectVersion> version = new ObjectProperty<ProjectVersion>(null, ProjectVersion.class);

		/*
		 * The query to get the reports for.
		 */
		private final ReportsQuery query = new ReportsQuery();

		/**
		 * Create the search criteria wrapper.
		 */
		public SearchCriteria() {
			query.reportStatuses = new HashSet<Report.Status>();

			version.addValueChangeListener(this);
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@Override
		public void valueChange(ValueChangeEvent event) {
			if (event.getProperty() == version) {
				query.projectVersion = version.getValue();
			}
		}

		/**
		 * Gets the query to search the reports with.
		 * @return	the query for reports.
		 */
		public ReportsQuery getQuery() {
			// It seems the ValueChangeListeners are called in the right order so we may skip this line.
			//query.projectVersion = version.getValue(); // Just to make sure.

			return query;
		}

		/*
		 * Sets the progect.
		 */
		void setProject(Project project) {
			query.project = project;
		}

		/*
		 * Gets the project.
		 */
		Project getProject() {
			return query.project;
		}

		/*
		 * Sets the assignee.
		 */
		void setAssignee(Reporter assignee) {
			query.reportAssignee = assignee;
		}

		/*
		 * Sets the status filter to Open status.
		 */
		void setStatusOpen() {
			query.reportStatuses.clear();
			query.reportStatuses.add(Status.OPEN);

			statusChanged();
		}

		/*
		 * Sets the status filter to all status.
		 */
		void setStatusAll() {
			query.reportStatuses.clear();

			statusChanged();
		}

		/*
		 * Sets the selected statuses.
		 */
		void setSelectedStatus(Collection<Status> statuses) {
			query.reportStatuses.clear();
			query.reportStatuses.addAll(statuses);

			statusChanged();
		}

		/*
		 * Gets the selected status list.
		 */
		Collection<Status> getSelectedStatus() {
			return query.reportStatuses;
		}

	}

	/*
	 * Selection listener for the report table rows.
	 */
	private class ReportTableSelectionListener implements ValueChangeListener {

		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void valueChange(ValueChangeEvent event) {
			Collection<Report> reports = (Collection<Report>) event.getProperty().getValue();

			System.out.println("event.getProperty(): " + event.getProperty().getClass().getName());
			System.out.println("event.getProperty().getValue(): " + event.getProperty().getValue().getClass().getName());
			System.out.println("event.getProperty().getValue(): " + event.getProperty().getValue());
			System.out.println("reportTable.getValue(): " + reportTable.getValue().getClass().getName());
			System.out.println("reportTable.getValue(): " + reportTable.getValue());

			reportEditor.setReport(reports.iterator().next()); // TODO: call setReports.
		}
	}

	/*
	 * Listen to commits from the editor.
	 */
	private class ReportEditorCommitObserver implements Observer, Serializable {

		/* (non-Javadoc)
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		@Override
		public void update(Observable o, Object arg) {
			System.out.println("arg: " + arg);
			System.out.println("arg: " + arg.getClass().getName());

			Report report = (Report) arg;

			reportTable.refreshRowCache(); // FIXME: This sucks, it should be automatically, or at least to refresh only that row.

			synchronized (this) { // Maybe this synch is to much.
				DataManager.getBugrapRepository().save(report);
				report.updateConsistencyVersion();
			}
		}

	}

}
