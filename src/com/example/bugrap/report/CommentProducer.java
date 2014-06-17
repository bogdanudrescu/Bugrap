package com.example.bugrap.report;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.example.utils.upload.HorizontalUploadGroup;
import com.example.utils.upload.UploadProducer.UploadProducerAdapter;
import com.example.utils.upload.UploadProgress;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * UI for the comment entity. Adds the comments/attachments.
 * 
 * @author bogdanudrescu
 */
@SuppressWarnings("serial")
public class CommentProducer extends Panel {

	/*
	 * The comment text property.
	 */
	private ObjectProperty<String> comment = new ObjectProperty<String>("");

	/*
	 * The upload listener.
	 */
	private UploadProducerHandler uploadListener;

	/*
	 * The upload group component.
	 */
	private HorizontalUploadGroup uploadGroup;

	/**
	 * Create a new comment editor.
	 */
	public CommentProducer() {

		// Button for actions.
		Button doneButton = new Button("Done"); // Add a V green icon ;)
		doneButton.addClickListener(new DoneButtonListener());
		//Button attachButton = new Button("Attachment...");
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickListener(new CancelButtonListener());

		// Other layout settings
		TextArea commentArea = new TextArea(comment);
		commentArea.setSizeFull();

		uploadListener = new UploadProducerHandler();
		uploadGroup = new HorizontalUploadGroup();
		uploadGroup.getProducer().addUploadProducerListener(uploadListener);

		HorizontalLayout buttonsLayout = new HorizontalLayout(doneButton, cancelButton);

		VerticalLayout layout = new VerticalLayout(commentArea, uploadGroup, buttonsLayout);
		layout.setMargin(true);
		layout.setExpandRatio(commentArea, 1);

		setContent(layout);
	}

	/*
	 * Listen to upload events.
	 */
	private class UploadProducerHandler extends UploadProducerAdapter {

		/*
		 * The list of current upload components. New Upload components are added when a upload starts.
		 */
		private List<UploadProgress> currentUploads = new LinkedList<>();

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProducer.UploadProducerAdapter#uploadDone(com.example.utils.upload.UploadProgress)
		 */
		@Override
		public void uploadDone(UploadProgress uploadProgress) {
			currentUploads.add(uploadProgress);
		}
	}

	/*
	 * Listen to the Done button click.
	 */
	private class DoneButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			if (delegate != null) {
				if (!StringUtils.isEmpty(comment.getValue())) {
					delegate.commentAdded(comment.getValue());
				}

				Iterator<UploadProgress> iterator = uploadListener.currentUploads.iterator();
				while (iterator.hasNext()) {
					UploadProgress upload = (UploadProgress) iterator.next();
					delegate.attachmentAdded(upload.getUploadFileName(), upload.getUploadBytes());
				}

			}
		}

	}

	/*
	 * Listen to the Cancel button click.
	 */
	private class CancelButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {

			// TODO: ask first, don't just clear the man's work.
			comment.setValue("");
		}

	}

	/*
	 * The delegate for the comment producer.
	 */
	private CommentProducerDelegate delegate;

	/**
	 * Sets the delegate.
	 * @param delegate	the comment producer's delegate.
	 */
	public void setDelegate(CommentProducerDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Delegate of the comment producer.
	 */
	public static interface CommentProducerDelegate {

		/**
		 * Called when a new comment should be added.
		 * @param comment	the comment to add.
		 * @return	true if the comment was successfully added.
		 */
		boolean commentAdded(String comment);

		/**
		 * Called when a new attachment should be added.
		 * @param attachmentName	the name of the attachment.
		 * @param attachment		the attachment date.
		 * @return	true if the attachment was successfully added.
		 */
		boolean attachmentAdded(String attachmentName, byte[] attachment);

	}

}
