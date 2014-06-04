package com.example.bugrap;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.hene.popupbutton.PopupButton;

import com.example.bugrap.data.DataManager;
import com.example.bugrap.data.LoginManager;
import com.example.bugrap.utils.ToggleButtonGroup;
import com.example.bugrap.utils.ToggleButtonGroup.ToggleButtonGroupEvent;
import com.example.bugrap.utils.ToggleButtonGroup.ToggleButtonGroupListener;
import com.example.bugrap.utils.Utils;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
	 * The reports data source.
	 */
	private Container reportsDataSource = new BeanItemContainer<Report>(Report.class);

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
		versionField.setPropertyDataSource(searchCriteria.version);
		versionField.addValueChangeListener(new VersionChangeListener());

		// Button groups.
		ToggleButtonGroup assigneesGroup = new ToggleButtonGroup("Only me", "Everyone");
		assigneesGroup.addListener(new AssigneeToggleListener());
		assigneesGroup.setCaption("Assignees");

		ToggleButtonGroup statusGroup = new ToggleButtonGroup("Open", "All kinds");
		statusGroup.setCaption("Status");

		PopupButton statusCustom = new PopupButton("Custom");
		statusCustom.setContent(createStatusCustomPopup());
		statusGroup.addButton(statusCustom);

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

		// Table with reports.
		Table reportsTable = new Table();
		reportsTable.setSizeFull();
		reportsTable.setContainerDataSource(reportsDataSource);
		reportsTable.setVisibleColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");

		reportsTable.setColumnHeader("priority", "Priority");
		reportsTable.setColumnHeader("type", "Type");
		reportsTable.setColumnHeader("summary", "Summary");
		reportsTable.setColumnHeader("assigned", "Assigned to");
		reportsTable.setColumnHeader("timestamp", "Last modified");
		reportsTable.setColumnHeader("reportedTimestamp", "Reported");

		// Date cell custom renderer.
		DateIntervalCell dateIntervalCell = new DateIntervalCell();
		reportsTable.addGeneratedColumn("timestamp", dateIntervalCell);
		reportsTable.addGeneratedColumn("reportedTimestamp", dateIntervalCell);

		layout.addComponent(reportsTable);
		layout.setExpandRatio(reportsTable, 1);
	}

	/*
	 * Generate the cell value for the date columns, showing how much time passed since the specified date.
	 */
	private class DateIntervalCell implements ColumnGenerator {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Table.ColumnGenerator#generateCell(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Object generateCell(Table source, Object itemId, Object columnId) {
			Property<Date> property = source.getContainerProperty(itemId, columnId);

			if (property != null && property.getType().equals(Date.class)) {
				return new Label(Utils.stringIntervalFromDate(property.getValue()));
			}

			return null;
		}

	}

	/*
	 * Refresh the reports according to the search criteria.
	 */
	private void refreshReports() {
		reportsDataSource.removeAllItems();

		ReportsQuery query = searchCriteria.getQuery();

		Set<Report> reports = DataManager.getBugrapRepository().findReports(query);
		for (Report report : reports) {
			reportsDataSource.addItem(report);
		}

		System.out.println("refreshReports for " + query + " output " + reports.size() + " results");
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
			refreshReports();
		}

	}

	/*
	 * Search criteria.
	 */
	class SearchCriteria implements Serializable {

		/*
		 * The free text search field.
		 */
		private Property<String> freeText = new ObjectProperty<String>("", String.class);

		/*
		 * The type of assignee (me or everyone).
		 */
		private Property<Reporter> assignee = new ObjectProperty<Reporter>(null, Reporter.class);

		/*
		 * The status of the report.
		 */
		private Property<Integer> reportStatus = new ObjectProperty<Integer>(0, Integer.class);

		/*
		 * The version for which to display reports.
		 */
		private Property<ProjectVersion> version = new ObjectProperty<ProjectVersion>(null, ProjectVersion.class);

		/*
		 * The current selected project.
		 */
		private Project project;

		/*
		 * Sets the progect.
		 */
		void setProject(Project project) {
			this.project = project;
		}

		/*
		 * Gets the project.
		 */
		Project getProject() {
			return project;
		}

		/*
		 * Sets the assignee.
		 */
		void setAssignee(Reporter assignee) {
			this.assignee.setValue(assignee);
		}

		/*
		 * Gets the assignee.
		 */
		Reporter getAssignee() {
			return assignee.getValue();
		}

		/*
		 * The query to get the reports for.
		 */
		private ReportsQuery query = new ReportsQuery();

		/**
		 * Create the search criteria wrapper.
		 */
		public SearchCriteria() {
			query.reportStatuses = new HashSet<Report.Status>();
		}

		/**
		 * Gets the query to search the reports with.
		 * @return	the query for reports.
		 */
		public ReportsQuery getQuery() {
			query.project = getProject();
			query.projectVersion = version.getValue();
			query.reportAssignee = getAssignee();
			query.reportStatuses.clear();

			return query;
		}

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
		if (projectVersionToSet != null) {
			setProjectVersion(projectVersionToSet);
		}
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
		if (!searchCriteria.getProject().equals(projectVersion.getProject())) {
			throw new IllegalArgumentException("The version do not belong to the selected project.");
		}

		searchCriteria.version.setValue(projectVersion);

	}

	/*
	 * The status option group.
	 */
	private OptionGroup statusOptionGroup;

	/*
	 * Create the status custom popup with all the options.
	 */
	private Component createStatusCustomPopup() {
		statusOptionGroup = new OptionGroup();
		statusOptionGroup.setMultiSelect(true);

		for (Status status : Status.values()) {
			statusOptionGroup.addItem(status);
		}

		return statusOptionGroup;
	}

	/**
	 * Handle the assignee selection.
	 * 
	 * @author bogdan
	 */
	private class AssigneeToggleListener implements ToggleButtonGroupListener {

		/* (non-Javadoc)
		 * @see com.example.bugrap.utils.ToggleButtonGroup.ToggleButtonGroupListener#selectedButtonChanged(com.example.bugrap.utils.ToggleButtonGroup.ToggleButtonGroupEvent)
		 */
		@Override
		public void selectedButtonChanged(ToggleButtonGroupEvent event) {
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

}
