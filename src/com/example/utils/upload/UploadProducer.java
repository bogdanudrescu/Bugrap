package com.example.utils.upload;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener;
import com.example.utils.upload.UploadProgressInfo.UploadStatus;

/**
 * Produce UploadProgressInfos as needed.
 * 
 * @author bogdanudrescu
 */
public class UploadProducer {

	/*
	 * Listen to each upload events.
	 */
	private UploadProgressInfoHandler handler = new UploadProgressInfoHandler();

	/*
	 * The initial number of upload components.
	 */
	private int initialUploads;

	/**
	 * Create the group with 1 Upload.
	 */
	public UploadProducer() {
		this(1);
	}

	/**
	 * Create the group with few initial uploads.
	 * @param initialUploads	the number of uploads to produce when this goes online.
	 */
	public UploadProducer(int initialUploads) {
		this.initialUploads = initialUploads;
	}

	/*
	 * The uploads.
	 */
	private List<UploadProgressInfo> uploads = new LinkedList<>(); // Maybe this is faster then ArrayList in our case...

	/**
	 * Gets the number of upload components.
	 * @return	the number of upload components.
	 */
	public int getUploadCount() {
		return uploads.size();
	}

	// TODO: add methods to retrieve the upload components.

	/**
	 * Start producing the uploads.
	 */
	public void produceUploads() {
		for (int i = 0; i < initialUploads; i++) {
			produceUpload();
		}
	}

	/*
	 * Produce a new upload.
	 */
	private void produceUpload() {
		UploadProgressInfo upload = new UploadProgressInfo(handler);
		addAllListenersToUpload(upload);

		uploads.add(upload);

		fireUploadProduced(upload);
	}

	/*
	 * Gets whether any upload is available.
	 */
	private boolean isAnyUploadAvailable() {
		EnumSet<UploadStatus> enumSet = EnumSet.noneOf(UploadStatus.class); // This should be slow though, but it's fancy enough.

		Iterator<UploadProgressInfo> iterator = uploads.iterator();
		while (iterator.hasNext()) {
			enumSet.add(iterator.next().getStatus());
		}

		return enumSet.contains(UploadStatus.NONE);
	}

	/*
	 * Manage the upload events.
	 */
	class UploadProgressInfoHandler implements UploadProgressInfoListener {

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener#shouldRemoveUploadComponent(com.example.utils.upload.UploadProgressInfo)
		 */
		@Override
		public void shouldRemoveUploadComponent(UploadProgressInfo uploadProgressInfo) {
			synchronized (UploadProducer.this) {

				// This doesn't need to synchronize
				uploads.remove(uploadProgressInfo);

				// uploadProgressInfo.removeUploadListener(this); // FIXME: either this or just remove the listeners automatically from the upload component directly. Any way there will be no further events...
			}
		}

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener#uploadStarted(com.example.utils.upload.UploadProgressInfo)
		 */
		@Override
		public void uploadStarted(UploadProgressInfo uploadProgressInfo) {
			synchronized (UploadProducer.this) { // Synch on UploadProducer.this otherwise we'll end up in a deadlock with the listener calls. 

				if (!isAnyUploadAvailable()) {
					produceUpload();
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener#uploadFailed(com.example.utils.upload.UploadProgressInfo)
		 */
		@Override
		public void uploadFailed(UploadProgressInfo uploadProgressInfo) {
		}

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener#uploadCanceled(com.example.utils.upload.UploadProgressInfo)
		 */
		@Override
		public void uploadCanceled(UploadProgressInfo uploadProgressInfo) {
		}

		/* (non-Javadoc)
		 * @see com.example.utils.upload.UploadProgressInfo.UploadProgressInfoListener#uploadDone(com.example.utils.upload.UploadProgressInfo)
		 */
		@Override
		public void uploadDone(UploadProgressInfo uploadProgressInfo) {
		}

	}

	/*
	 * The listeners list.
	 */
	private List<UploadProducerListener> listeners = new LinkedList<>();

	/**
	 * Adds an upload listener.
	 * @param listener	the listener to add.
	 */
	public synchronized void addUploadListener(UploadProducerListener listener) {
		listeners.add(listener);

		addListenerToAllUploads(listener);
	}

	/**
	 * Adds an upload listener.
	 * @param listener	the listener to add.
	 */
	public synchronized void removeUploadListener(UploadProducerListener listener) {
		listeners.remove(listener);

		removeListenerFromAllUploads(listener);
	}

	/*
	 * Add the specified listener to all uploads.
	 */
	private synchronized void addListenerToAllUploads(UploadProducerListener listener) {
		for (UploadProgressInfo upload : uploads) {
			upload.addUploadListener(listener);
		}
	}

	/*
	 * Add the specified listener to all uploads.
	 */
	private synchronized void removeListenerFromAllUploads(UploadProducerListener listener) {
		for (UploadProgressInfo upload : uploads) {
			upload.removeUploadListener(listener);
		}
	}

	/*
	 * Add all listeners to the specified upload.
	 */
	private synchronized void addAllListenersToUpload(UploadProgressInfo upload) {
		for (UploadProducerListener listener : listeners) {
			upload.addUploadListener(listener);
		}
	}

	/**
	 * Remove all the listeners from the specified upload.
	 * @deprecated dangerous method to remove all listeners.
	 */
	private synchronized void removeAllListenersFromUpload(UploadProgressInfo upload) {
		for (UploadProducerListener listener : listeners) {
			upload.removeUploadListener(listener);
		}
	}

	/**
	 * Produce an upload component.
	 * @param uploadProgressInfo	the component produced.
	 */
	protected synchronized void fireUploadProduced(UploadProgressInfo uploadProgressInfo) {
		for (UploadProducerListener listener : listeners) {
			listener.uploadProduced(uploadProgressInfo);
		}
	}

	/**
	 * Receive notifications when the uploads are produced and when files are uploaded.
	 */
	public interface UploadProducerListener extends UploadProgressInfoListener {

		/**
		 * Produce an upload component.
		 * @param uploadProgressInfo	the component produced.
		 */
		void uploadProduced(UploadProgressInfo uploadProgressInfo);

	}

}
