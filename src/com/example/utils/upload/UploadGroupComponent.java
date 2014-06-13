package com.example.utils.upload;

import com.example.utils.upload.UploadProducer.UploadProducerListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

/**
 * Layout {@link UploadProgressInfo} components which are produced by a {@link UploadProducer}.
 * 
 * Right now dummy class just to test the producer.
 * 
 * @author bogdanudrescu
 */
public class UploadGroupComponent extends Panel {

	/*
	 * Produce uploads.
	 */
	private UploadProducer producer = new UploadProducer(2);

	private UploadHandler handler = new UploadHandler();

	private HorizontalLayout layout = new HorizontalLayout();

	/**
	 * Create a default upload group component.
	 */
	public UploadGroupComponent() {
		producer.addUploadListener(handler);
		producer.produceUploads();

		layout.setSizeFull();
		setContent(layout);
	}

	private class UploadHandler implements UploadProducerListener {

		@Override
		public void uploadProduced(UploadProgressInfo uploadProgressInfo) {
			layout.addComponent(uploadProgressInfo);

		}

		@Override
		public void shouldRemoveUploadComponent(UploadProgressInfo uploadProgressInfo) {
			layout.removeComponent(uploadProgressInfo);
		}

		@Override
		public void uploadStarted(UploadProgressInfo uploadProgressInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void uploadFailed(UploadProgressInfo uploadProgressInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void uploadCanceled(UploadProgressInfo uploadProgressInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void uploadDone(UploadProgressInfo uploadProgressInfo) {
			String caption = "UPLOAD DONE " + uploadProgressInfo.getUploadFileName();

			System.out.println(caption);
			Notification.show(caption);
		}

	}

}
