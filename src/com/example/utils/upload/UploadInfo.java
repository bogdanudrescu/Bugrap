package com.example.utils.upload;

import com.vaadin.server.ClassResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;

/**
 * Component to show the progress of a file. It provides also cancel input from user through the delegate.
 * <br/>
 * This component actually uploads no file, but only show the progress. The progress should be updated by the actual uploader.
 * 
 * @author bogdanudrescu
 */
@SuppressWarnings("serial")
public class UploadInfo extends CustomComponent {

	/*
	 * The label with the file name.
	 */
	private Label nameLabel = new Label();

	/*
	 * The progress bar.
	 */
	private ProgressBar progressBar = new ProgressBar();

	/*
	 * The cancel button.
	 */
	private Button cancelButton = new Button(new ClassResource(this.getClass(), "cancel.png"));

	/*
	 * The size of the file in bytes.
	 */
	private long bytesCount = -1;

	/*
	 * The composition root.
	 */
	private HorizontalLayout layout;

	/**
	 * Create an upload progress component for a file with the specified name.
	 * @param fileName		the name of the file being uploaded.
	 * @param bytesCount	the size of the file in bytes.
	 */
	public UploadInfo(String fileName, long bytesCount) {
		nameLabel.setValue(fileName);

		if (bytesCount < 0) {
			progressBar.setIndeterminate(true);
		}

		cancelButton.addClickListener(new CancelButtonListener());

		layout = new HorizontalLayout();
		layout.addComponent(nameLabel);
		layout.addComponent(progressBar);
		layout.addComponent(cancelButton);

		setCompositionRoot(layout);
	}

	/**
	 * Sets the count of the bytes read so far.
	 * @param currentBytesCount	the current count of the bytes read. 
	 */
	public void setCurrentBytesRead(long currentBytesCount) {
		progressBar.setValue((float) currentBytesCount / bytesCount);
	}

	/**
	 * Inform the component that the file was successfully uploaded.
	 */
	public void setProgressDone() {
		layout.removeComponent(progressBar);
	}

	/**
	 * Inform the component that the file failed to upload.
	 */
	public void setProgressFail() {
		layout.removeComponent(progressBar);

		layout.addComponent(new Label("Failed"), 1);
	}

	/*
	 * Listen to the cancel event and notify the delegate to cancel the upload.
	 */
	private class CancelButtonListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			// TODO: ask again if the user is sure he wants to cancel.

			delegate.cancelUpload(UploadInfo.this);
		}

	}

	/*
	 * The upload info delegate.
	 */
	private UploadInfoDelegate delegate;

	/**
	 * Sets the delegate for this upload info component.
	 * @param delegate	the delegate to handle the cancel action.
	 */
	public void setDelegate(UploadInfoDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Delegate to inform that the upload should cancel.
	 */
	public static interface UploadInfoDelegate {

		/**
		 * Call when the user wishes to cancel the upload.
		 * @param uploadInfo	the {@link UploadInfo} source object.
		 */
		void cancelUpload(UploadInfo uploadInfo);

		/**
		 * Called in case the upload failed and user wants to retry.
		 * @param uploadInfo	the {@link UploadInfo} source object.
		 */
		void retryUpload(UploadInfo uploadInfo); // TODO: Call this.

	}

}
