package com.example.bugrap.widgetset.client.prioritybox;

import com.example.prioritybox.PriorityBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(PriorityBox.class)
public class PriorityBoxConnector extends AbstractComponentConnector {

	PriorityBoxServerRpc rpc = RpcProxy.create(PriorityBoxServerRpc.class, this);

	public PriorityBoxConnector() {
		registerRpc(PriorityBoxClientRpc.class, new PriorityBoxClientRpc() {
			public void alert(String message) {
				// TODO Do something useful
				Window.alert(message);
			}
		});

	}

	@Override
	protected Widget createWidget() {
		return GWT.create(PriorityBoxWidget.class);
	}

	@Override
	public PriorityBoxWidget getWidget() {
		return (PriorityBoxWidget) super.getWidget();
	}

	@Override
	public PriorityBoxState getState() {
		return (PriorityBoxState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// TODO do something useful
		final String text = getState().text;
	}

}
