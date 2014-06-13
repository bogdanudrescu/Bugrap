package com.example.utils.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.example.utils.upload.UploadInfo.UploadInfoDelegate;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

/**
 * Files upload component, including the progress and file info.
 * 
 * @author bogdanudrescu
 */
/**
 * @author bogdanudrescu
 *
 */
@SuppressWarnings("serial")
public class UploadProgressInfo extends CustomComponent {

	/*
	 * The upload component.
	 */
	private Upload upload;

	/*
	 * Handle the upload component events.
	 */
	private UploadEventsHandler uploadEventsHandler = new UploadEventsHandler();

	/**
	 * Create an upload progress info component.
	 */
	public UploadProgressInfo() {
		this(null);
	}

	/**
	 * Create an upload progress info component. 
	 * @param listener	the initial upload listener.
	 */
	public UploadProgressInfo(UploadProgressInfoListener listener) {
		addUploadListener(listener);

		upload = new Upload();
		upload.setReceiver(uploadEventsHandler);
		upload.addStartedListener(uploadEventsHandler);
		upload.addProgressListener(uploadEventsHandler);
		upload.addSucceededListener(uploadEventsHandler);
		upload.addFailedListener(uploadEventsHandler);

		setCompositionRoot(upload);
	}

	/**
	 * Upload status.
	 */
	public static enum UploadStatus {

		/**
		 * The upload didn't start.
		 */
		NONE,

		/**
		 * Upload the date now.
		 */
		UPLOADING,

		/**
		 * Canceled by the user.
		 */
		CANCELED,

		/**
		 * Failed to upload.
		 */
		FAILED,

		/**
		 * Upload succeeded.
		 */
		DONE
	}

	/**
	 * The status of the upload.
	 */
	private volatile UploadStatus status = UploadStatus.NONE;

	/**
	 * Gets the current status of the upload.
	 * @return	the current status of the upload.
	 */
	public UploadStatus getStatus() {
		return status;
	}

	/**
	 * Sets the custom receiver.
	 * @param receiver	the custom receiver.
	 */
	public void setReceiver(Receiver receiver) {
		uploadEventsHandler.receiver = receiver;
	}

	/*
	 * Used to upload attachment files.
	 */
	private class UploadEventsHandler implements Receiver, StartedListener, ProgressListener, SucceededListener, FailedListener, UploadInfoDelegate {

		/*
		 * The upload info component.
		 */
		private UploadInfo uploadInfo;

		/*
		 * The name of the file being uploaded.
		 */
		private String filename;

		/*
		 * The mime type of the file being uploaded.
		 */
		private String mimeType;

		/*
		 * A custom receiver.
		 */
		private Receiver receiver;

		/*
		 * The output stream where to write the response.
		 */
		private ByteArrayOutputStream stream;

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.Receiver#receiveUpload(java.lang.String, java.lang.String)
		 */
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {

			this.stream = null;
			OutputStream stream = null;

			if (receiver != null) {
				stream = receiver.receiveUpload(filename, mimeType);
			}

			if (stream == null) {
				this.stream = new ByteArrayOutputStream();
				stream = this.stream;
			}

			return stream;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.StartedListener#uploadStarted(com.vaadin.ui.Upload.StartedEvent)
		 */
		@Override
		public void uploadStarted(StartedEvent event) {
			if (uploadInfo == null) {
				uploadInfo = new UploadInfo(event.getFilename(), event.getContentLength());
				uploadInfo.setDelegate(this);
			}

			setCompositionRoot(uploadInfo);

			status = UploadStatus.UPLOADING;
			fireUploadStarted();
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.ProgressListener#updateProgress(long, long)
		 */
		@Override
		public void updateProgress(long readBytes, long contentLength) {
			System.out.println("updateProgress: " + readBytes + ", " + contentLength);

			uploadInfo.setCurrentBytesRead(readBytes);
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.SucceededListener#uploadSucceeded(com.vaadin.ui.Upload.SucceededEvent)
		 */
		@Override
		public void uploadSucceeded(SucceededEvent event) {

			System.out.println("uploadSucceeded");

			uploadInfo.setProgressDone();

			Notification.show("Upload succedded");

			status = UploadStatus.DONE;
			fireUploadDone();
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Upload.FailedListener#uploadFailed(com.vaadin.ui.Upload.FailedEvent)
		 */
		@Override
		public void uploadFailed(FailedEvent event) {

			uploadInfo.setProgressFail();

			Notification.show("Upload failed", Type.ERROR_MESSAGE);

			closeStream();

			status = UploadStatus.FAILED;
			fireUploadFailed();
		}

		/* (non-Javadoc)
		 * @see com.example.utils.UploadInfo.UploadInfoDelegate#cancelUpload(com.example.utils.UploadInfo)
		 */
		@Override
		public void cancelUpload(UploadInfo uploadInfo) {
			if (upload.isUploading()) {

				upload.interruptUpload();

				setCompositionRoot(upload);

				closeStream();

				status = UploadStatus.CANCELED;
				fireUploadCanceled();

			} else {
				fireShouldRemoveUploadComponent();
			}
		}

		/* (non-Javadoc)
		 * @see com.example.utils.UploadInfo.UploadInfoDelegate#retryUpload(com.example.utils.UploadInfo)
		 */
		@Override
		public void retryUpload(UploadInfo uploadInfo) {
			// TODO Auto-generated method stub
		}

		/*
		 * Close the output stream where the data is saved.
		 */
		private void closeStream() {
			try {
				if (stream != null) {
					stream.close();
					stream = null;
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * Gets the name of the uploaded file.
	 * @return	the name of the uploaded file.
	 */
	public String getUploadFileName() {
		checkConsistency();
		return uploadEventsHandler.filename;
	}

	/**
	 * Gets the upload file mime type.
	 * @return	the upload file mime type.
	 */
	public String getUploadMimeType() {
		checkConsistency();
		return uploadEventsHandler.mimeType;
	}

	/**
	 * Gets the download bytes.
	 * @return	the download bytes.
	 */
	public byte[] getUploadBytes() {
		checkConsistency();
		return uploadEventsHandler.stream.toByteArray();
	}

	/*
	 * Check whether the upload is consistent and may provide the data.
	 */
	private void checkConsistency() {
		if (status != UploadStatus.DONE) {
			throw new UploadException("Inconsistent uploaded data. Status: " + status);

		} else if (uploadEventsHandler.stream == null) {
			throw new UploadException(
					"OutputStream and other upload info handled in the UploadProgressInfoDelegate already. Check your delegate implementation and access the data in receiveUpload method you implemented.");
		}

	}

	/*
	 * The delegate.
	 */
	private List<UploadProgressInfoListener> listeners = new LinkedList<>();

	/**
	 * Adds an upload listener.
	 * @param listener	the listener to add.
	 */
	public synchronized void addUploadListener(UploadProgressInfoListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the specified upload listener.
	 * @param listener	the listener to remove.
	 */
	public synchronized void removeUploadListener(UploadProgressInfoListener listener) {
		listeners.remove(listener);
	}

	/*
	 * Inform the API user that the upload should be removed.
	 */
	protected synchronized void fireShouldRemoveUploadComponent() {
		Iterator<UploadProgressInfoListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().shouldRemoveUploadComponent(this);
		}

		// We won't send any events so no need for listeners from now on.
		listeners.clear();
	}

	/*
	 * Called when the upload starts. 
	 */
	protected synchronized void fireUploadStarted() {
		Iterator<UploadProgressInfoListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().uploadStarted(this);
		}
	}

	/*
	 * Called when the upload failed. 
	 */
	protected synchronized void fireUploadFailed() {
		Iterator<UploadProgressInfoListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().uploadFailed(this);
		}
	}

	/*
	 * Called when the upload is cancel by the user. 
	 */
	protected synchronized void fireUploadCanceled() {
		Iterator<UploadProgressInfoListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().uploadCanceled(this);
		}
	}

	/*
	 * Called when the upload is being successful, hopefully. 
	 */
	protected synchronized void fireUploadDone() {
		Iterator<UploadProgressInfoListener> iterator = listeners.iterator(); // TODO: This how to write all fire methods with one call, without the iteration loop here.
		while (iterator.hasNext()) {
			iterator.next().uploadDone(this);
		}
	}

	/**
	 * Notify about certain action related directly to the {@link UploadProgressInfo}.
	 * <br/>
	 * In the {@link Receiver#receiveUpload(String, String)} returns null, then the {@link UploadProgressInfo} 
	 * will buffer and provide the file content through 
	 */
	public interface UploadProgressInfoListener {
		// TODO: Should have my own method for what Receiver does, but we'll see...
		// OR: Create a model for the file upload so that one can bind it easily with
		// external sources... though what can be better then an OutputStream...?

		/**
		 * Inform the API user that the upload should be removed.
		 * @param uploadProgressInfo	the {@link UploadProgressInfo} component.
		 */
		void shouldRemoveUploadComponent(UploadProgressInfo uploadProgressInfo);

		/**
		 * Called when the upload starts. 
		 * @param uploadProgressInfo	the {@link UploadProgressInfo} component.
		 */
		void uploadStarted(UploadProgressInfo uploadProgressInfo);

		/**
		 * Called when the upload failed. 
		 * @param uploadProgressInfo	the {@link UploadProgressInfo} component.
		 */
		void uploadFailed(UploadProgressInfo uploadProgressInfo);

		/**
		 * Called when the upload is cancel by the user. 
		 * @param uploadProgressInfo	the {@link UploadProgressInfo} component.
		 */
		void uploadCanceled(UploadProgressInfo uploadProgressInfo);

		/**
		 * Called when the upload is being successful, hopefully. 
		 * @param uploadProgressInfo	the {@link UploadProgressInfo} component.
		 */
		void uploadDone(UploadProgressInfo uploadProgressInfo);

	}

	/**
	 * Exception thrown when the upload is in an inconsistent state and data about the upload is being accessed.
	 */
	public static class UploadException extends RuntimeException {

		/**
		 * Create an exception to notify that the data is still being uploaded.
		 */
		public UploadException(String message) {
			super(message);
		}

	}

}
