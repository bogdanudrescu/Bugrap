package com.example.bugrap.report;

import java.io.Serializable;
import java.util.Collection;
import java.util.Observable;

import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.example.bugrap.data.DataManager;
import com.example.bugrap.report.CommentProducer.CommentProducerDelegate;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

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

	/*
	 * The title label. I couldn't bind it to the model...
	 */
	private Label titleLabel;

	/*
	 * The breadcrumbs label.
	 */
	private Label breadcrumbsLabel;

	/*
	 * The comments view.
	 */
	private CommentsView commentsView;

	/**
	 * Create a new report editor.
	 */
	public ReportEditor() {

		binder.setBuffered(true); // make sure it's buffered (by default seems to be true anyway).

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		breadcrumbsArea = createBreadcrumbsArea();
		layout.addComponent(breadcrumbsArea);

		layout.addComponent(createTitleArea());
		layout.addComponent(createPropertiesArea());

		Component descriptionArea = createCommentsArea();
		layout.addComponent(descriptionArea);
		layout.setExpandRatio(descriptionArea, 0.5f); // FIXME This doesn't work when resize.

		Component commentArea = createNewCommentArea();
		layout.addComponent(commentArea);
		layout.setExpandRatio(commentArea, 0.5f);

		setContent(layout);
	}

	/*
	 * The breadcrumbs area.
	 */
	private Component breadcrumbsArea;

	/**
	 * Sets the visible state of the breadcrumbs.
	 * @param breadcrumbsVisible	the new visible state of the breadcrumbs.
	 */
	public void setBreadcrumbsVisible(boolean breadcrumbsVisible) {
		breadcrumbsArea.setVisible(breadcrumbsVisible);
	}

	/*
	 * Create the a non navigation path. 
	 */
	private Component createBreadcrumbsArea() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		breadcrumbsLabel = new Label();

		layout.addComponent(breadcrumbsLabel);

		return layout;
	}

	/*
	 * Create the title area.
	 */
	private Component createTitleArea() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		Button button = new Button("Open in new window");

		titleLabel = new Label();
		titleLabel.setSizeFull();

		layout.addComponent(button);
		layout.addComponent(titleLabel);

		layout.setExpandRatio(titleLabel, 1);

		return layout;
	}

	/*
	 * Create the top bar.
	 */
	private Component createPropertiesArea() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

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

		layout.addComponent(priorityComboBox);
		layout.addComponent(typeComboBox);
		layout.addComponent(statusComboBox);
		layout.addComponent(assigneeComboBox);
		layout.addComponent(versionComboBox);
		layout.addComponent(updateButton);
		layout.addComponent(revertButton);
		return layout;
	}

	/*
	 * Create the comment area.
	 */
	private Component createNewCommentArea() {

		CommentProducer commentProducer = new CommentProducer();
		commentProducer.setSizeFull();
		commentProducer.setDelegate(new CommentProducerDelegateImpl());

		return commentProducer;
	}

	/*
	 * Create the description area.
	 */
	private Component createCommentsArea() {
		commentsView = new CommentsView();
		commentsView.setSizeFull();

		return commentsView;
	}

	private class CommentProducerDelegateImpl implements CommentProducerDelegate {

		/* (non-Javadoc)
		 * @see com.example.bugrap.report.CommentProducer.CommentProducerDelegate#commentAdded(java.lang.String)
		 */
		@Override
		public boolean commentAdded(String comment) {
			commentsView.addComment(comment);
			return true;
		}

		/* (non-Javadoc)
		 * @see com.example.bugrap.report.CommentProducer.CommentProducerDelegate#attachmentAdded(java.lang.String, byte[])
		 */
		@Override
		public boolean attachmentAdded(String attachmentName, byte[] attachment) {
			commentsView.addAttachment(attachmentName, attachment);
			return true;
		}

	}

	/**
	 * Sets the report object.
	 * @param report	the report to set.
	 */
	public void setReport(Report report) {
		// Set the value list before setting the binding.
		versionComboBox.setContainerDataSource(new BeanItemContainer<ProjectVersion>(ProjectVersion.class, DataManager.getBugrapRepository()
				.findProjectVersions(report.getProject())));

		// Sets the title.
		titleLabel.setCaption(report.getSummary());

		// Sets the breadcrumbs title.
		updateBreadcrumbs(report);

		//this.report = report;
		binder.setItemDataSource(new BeanItem<Report>(report));

		commentsView.setReport(report);
	}

	/*
	 * Update the breadcrumbs label from the specified report.
	 * 
	 * Maybe we should keep the report as a member, rather then passing it as an argument to this method.
	 */
	private void updateBreadcrumbs(Report report) {
		String breadcrumbs = report.getProject().getName();
		if (report.getVersion() != null) {
			breadcrumbs += " > " + report.getVersion().getVersion();
		}
		breadcrumbsLabel.setCaption(breadcrumbs);
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
				Report report = item.getBean();

				commitObservable.notifyObservers(report); // Maybe we should only notify and pass no object.

				// Or maybe call setReport... after the notify...
				updateBreadcrumbs(report);

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
	private Observable commitObservable = new CommitObservable();

	/*
	 * Used to notify when the commit is done.
	 */
	private class CommitObservable extends Observable implements Serializable {

		/* (non-Javadoc)
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		public void notifyObservers(Object arg) {
			super.setChanged();
			super.notifyObservers(arg);
		};
	}

	/**
	 * Gets the commit observable used to notify when the commit is done.
	 * @return	the commit observable.
	 */
	public Observable getCommitObservable() {
		return commitObservable;
	}

}
