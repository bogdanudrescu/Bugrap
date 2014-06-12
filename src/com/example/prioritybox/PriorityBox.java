package com.example.prioritybox;

import com.example.bugrap.widgetset.client.prioritybox.PriorityBoxClientRpc;
import com.example.bugrap.widgetset.client.prioritybox.PriorityBoxServerRpc;
import com.vaadin.shared.MouseEventDetails;
import com.example.bugrap.widgetset.client.prioritybox.PriorityBoxState;

public class PriorityBox extends com.vaadin.ui.AbstractComponent {

	private PriorityBoxServerRpc rpc = new PriorityBoxServerRpc() {
		private int clickCount = 0;

		public void clicked(MouseEventDetails mouseDetails) {
			// nag every 5:th click using RPC
			if (++clickCount % 5 == 0) {
				getRpcProxy(PriorityBoxClientRpc.class).alert(
						"Ok, that's enough!");
			}
			// update shared state
			getState().text = "You have clicked " + clickCount + " times";
		}
	};  

	public PriorityBox() {
		registerRpc(rpc);
	}

	@Override
	public PriorityBoxState getState() {
		return (PriorityBoxState) super.getState();
	}
}
