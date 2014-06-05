package com.example.bugrap;

import java.util.Collection;
import java.util.Observable;

import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.example.bugrap.data.DataManager;
import com.example.utils.Utils;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

/**
 * The edit page for a report.
 * 
 * @author bogdanudrescu
 */
@SuppressWarnings("serial")
public class ReportEditor extends Panel {

	/*
	 * The report object.
	 */
	//private Report report;

	/*
	 * The binder between the report item and the components.
	 */
	private FieldGroup binder = new FieldGroup();

	/*
	 * The version combo box. We need it each time when a new report is set so we can change 
	 * the list of versions according to the versions of the report's project.
	 */
	private ComboBox versionComboBox;

	/**
	 * Create a new report editor.
	 */
	public ReportEditor() {

		binder.setBuffered(true); // make sure it's buffered (by default seems to be true anyway).

		ComboBox priorityComboBox = new ComboBox("Priority");
		priorityComboBox.setContainerDataSource(Utils.createValueListContainerFromEnum(Priority.class, Priority.values()));
		binder.bind(priorityComboBox, "priority");

		ComboBox typeComboBox = new ComboBox("Type");
		typeComboBox.setContainerDataSource(Utils.createValueListContainerFromEnum(Type.class, Type.values()));
		binder.bind(typeComboBox, "type");

		ComboBox statusComboBox = new ComboBox("Status");
		statusComboBox.setContainerDataSource(Utils.createValueListContainerFromEnum(Status.class, Status.values()));
		binder.bind(statusComboBox, "status");

		ComboBox assigneeComboBox = new ComboBox("Assigned to");
		assigneeComboBox.setContainerDataSource(new BeanItemContainer<Reporter>(Reporter.class, DataManager.getBugrapRepository().findReporters()));
		binder.bind(assigneeComboBox, "assigned");

		versionComboBox = new ComboBox("Version");
		binder.bind(versionComboBox, "version");

		Button updateButton = new Button("Update");
		updateButton.addClickListener(new UpdateButtonListener());

		Button revertButton = new Button("Revert");
		revertButton.addClickListener(new RevertButtonListener());

		HorizontalLayout layout = new HorizontalLayout();
		setContent(layout);

		layout.addComponent(priorityComboBox);
		layout.addComponent(typeComboBox);
		layout.addComponent(statusComboBox);
		layout.addComponent(assigneeComboBox);
		layout.addComponent(versionComboBox);
		layout.addComponent(updateButton);
		layout.addComponent(revertButton);
	}

	/**
	 * Sets the report object.
	 * @param report	the report to set.
	 */
	public void setReport(Report report) {
		// Set the value list before setting the binding.
		versionComboBox.setContainerDataSource(new BeanItemContainer<ProjectVersion>(ProjectVersion.class, DataManager.getBugrapRepository()
				.findProjectVersions(report.getProject())));

		//this.report = report;
		binder.setItemDataSource(new BeanItem<Report>(report));
	}

	/**
	 * Sets the report object.
	 * @param report	the report to set.
	 */
	public void setReports(Collection<Report> reports) {

		// TODO finish this
		PropertysetItem item = new PropertysetItem();

		binder.setItemDataSource(item);
	}

	/*
	 * Listen to the update button clicks.
	 */
	private class UpdateButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void buttonClick(ClickEvent event) {
			try {
				binder.commit();

				BeanItem<Report> item = (BeanItem<Report>) binder.getItemDataSource();
				commitObservable.notifyObservers(item.getBean()); // Maybe we should only notify and pass no object.
			} catch (CommitException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * Listen to the revert button clicks.
	 */
	private class RevertButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			binder.discard();
		}

	}

	/*
	 * Used to notify when the commit is done.
	 */
	private Observable commitObservable = new Observable() {

		/* (non-Javadoc)
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		public void notifyObservers(Object arg) {
			super.setChanged();
			super.notifyObservers(arg);
		};
	};

	/**
	 * Gets the commit observable used to notify when the commit is done.
	 * @return	the commit observable.
	 */
	public Observable getCommitObservable() {
		return commitObservable;
	}

}
