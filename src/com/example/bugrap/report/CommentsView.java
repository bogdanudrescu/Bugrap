package com.example.bugrap.report;

import java.util.Date;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Comment.Type;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.data.DataManager;
import com.example.bugrap.data.LoginManager;
import com.example.bugrap.resources.BugrapResources;
import com.example.utils.Utils;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

/**
 * Displays the comments list.
 * 
 * @author bogdanudrescu
 */
@SuppressWarnings("serial")
public class CommentsView extends Panel {

	/*
	 * The components layout.
	 */
	private GridLayout layout;

	/**
	 * Creates the comments view.
	 */
	public CommentsView() {
		layout = new GridLayout(2, 1);
		layout.setSizeFull();
	}

	/*
	 * The current report to display comments for.
	 */
	private Report report;

	/*
	 * The current row to know where to add a new comment.
	 */
	private int currentRow;

	/**
	 * Sets the report to show the comments for.
	 * @param report	the report to show the comments for.
	 */
	public synchronized void setReport(Report report) {
		this.report = report;

		layout.removeAllComponents();

		currentRow = 0;
		List<Comment> comments = DataManager.getBugrapRepository().findComments(report);
		for (Comment comment : comments) {

			layoutComment(comment);
		}

		System.out.println("rows: " + currentRow);
	}

	/*
	 * Add comment on the layout.
	 */
	private void layoutComment(Comment comment) {
		Image image = new Image(null, BugrapResources.getInstance().getResource("girl.png"));
		layout.addComponent(image, 0, currentRow);

		Label personLabel = new Label(comment.getAuthor().getName() + "(" + Utils.stringIntervalFromDateUntilNow(comment.getTimestamp()) + ")");
		layout.addComponent(personLabel, 1, currentRow);

		currentRow++;

		TextArea commentArea = new TextArea(null, comment.getComment());
		layout.addComponent(commentArea, 1, currentRow);

		currentRow++;
	}

	/**
	 * Adds a comment to the list and store it in the persistence layer.
	 * @param attachmentName	the name of the attachment.
	 * @param attachment		the attachment data.
	 */
	public synchronized void addAttachment(String attachmentName, byte[] attachment) {
		Comment comment = new Comment();
		comment.setAttachmentName(attachmentName);
		comment.setAttachment(attachment);
		comment.setType(Type.COMMENT);

		setDefaultsAndStore(comment);
	}

	/**
	 * Adds a comment to the list and store it in the persistence layer.
	 * @param commentText	the new comment to add.
	 */
	public synchronized void addComment(String commentText) {
		Comment comment = new Comment();
		comment.setComment(commentText);
		comment.setType(Type.COMMENT);

		setDefaultsAndStore(comment);
	}

	/*
	 * Set the defaults for this comment.
	 */
	private synchronized void setDefaultsAndStore(Comment comment) {
		comment.setReport(report);
		comment.setAuthor(LoginManager.getManager().getUser());
		comment.setTimestamp(new Date());

		DataManager.getBugrapRepository().save(comment);
		comment.updateConsistencyVersion();

		layoutComment(comment);
	}

}
