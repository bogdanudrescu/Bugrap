package com.example.bugrap.report;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.utils.upload.UploadGroupComponent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

/**
 * UI for the comment entity. Adds the comments/attachments.
 * 
 * @author bogdanudrescu
 */
public class CommentProducer extends Panel {

	/*
	 * The comment text property.
	 */
	private ObjectProperty<String> comment = new ObjectProperty<String>("");

	/**
	 * Create a new comment editor.
	 */
	public CommentProducer() {

		// Button for actions.
		Button doneButton = new Button("Done"); // Add a V green icon ;)
		//Button attachButton = new Button("Attachment...");
		Button cancelButton = new Button("Cancel");

		uploadPanel = new Panel();

		// Other layout settings
		TextArea commentArea = new TextArea(comment);
		commentArea.setSizeFull();

		UploadGroupComponent uploadGroupComponent = new UploadGroupComponent();

		HorizontalLayout buttonsLayout = new HorizontalLayout(doneButton, uploadPanel, cancelButton);

		VerticalLayout layout = new VerticalLayout(commentArea, uploadGroupComponent, buttonsLayout);
		layout.setMargin(true);
		layout.setExpandRatio(commentArea, 1);

		setContent(layout);

		//addNewUploadComponent();
	}

	/*
	 * The upload panel to hold only one upload instance.
	 */
	private Panel uploadPanel;

	/*
	 * Add a new upload component when the upload starts for the current one.
	 */
	private void addNewUploadComponent() {
		AttachmentReceiver attachmentReceiver = new AttachmentReceiver();

		Upload upload = new Upload();
		upload.setReceiver(attachmentReceiver);
		upload.addStartedListener(attachmentReceiver);
		upload.addProgressListener(attachmentReceiver);
		upload.addSucceededListener(attachmentReceiver);
		upload.addFailedListener(attachmentReceiver);

		uploadPanel.setContent(upload);
	}

	/*
	 * The list of current upload components. New Upload components are added when a upload starts.
	 */
	private List<Upload> currentUploads = new ArrayList<>();

	/*
	 * Used to upload attachment files.
	 */
	private class AttachmentReceiver implements Receiver, StartedListener, ProgressListener, SucceededListener, FailedListener {

		/*
		 * The progress bar.
		 */
		private ProgressBar progressBar;

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.Receiver#receiveUpload(java.lang.String, java.lang.String)
		 */
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.StartedListener#uploadStarted(com.vaadin.ui.Upload.StartedEvent)
		 */
		@Override
		public void uploadStarted(StartedEvent event) {
			currentUploads.add(event.getUpload());

			addNewUploadComponent();

			progressBar = new ProgressBar();
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.ProgressListener#updateProgress(long, long)
		 */
		@Override
		public void updateProgress(long readBytes, long contentLength) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.SucceededListener#uploadSucceeded(com.vaadin.ui.Upload.SucceededEvent)
		 */
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			currentUploads.remove(event.getUpload());

			Notification.show("Upload succedded");
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.FailedListener#uploadFailed(com.vaadin.ui.Upload.FailedEvent)
		 */
		@Override
		public void uploadFailed(FailedEvent event) {
			currentUploads.remove(event.getUpload());

			Notification.show("Upload failed", Type.ERROR_MESSAGE);
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
			// TODO Auto-generated method stub

		}

	}

	/*
	 * Listen to the Attach button click.
	 */
	private class AttachButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {

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
