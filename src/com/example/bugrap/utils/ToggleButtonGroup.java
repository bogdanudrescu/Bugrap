package com.example.bugrap.utils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.buttongroup.ButtonGroup;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

/**
 * Implement toggle buttons behavior on top of ButtonGroup.
 * 
 * TODO: Add some method APIs like addButton(String) and to create the buttons automatically.
 */
@SuppressWarnings("serial")
public class ToggleButtonGroup extends ButtonGroup {

	/*
	 * The click listener that will handle the toggle functionality.
	 */
	private ToggleClickListener toggleClickListener = new ToggleClickListener();

	/*
	 * The list of listeners to send the events to.
	 * 
	 * FIXME: Don't like to use the EventRouter because it uses reflection. That's slow.
	 */
	private List<ToggleButtonGroupListener> listeners;

	/**
	 * Create a toggle buttons group.
	 */
	public ToggleButtonGroup() {
	}

	/**
	 * Creates a toggle buttons group with some default buttons.
	 * @param buttonCaptions	the captions of the default buttons to add to the group.
	 */
	public ToggleButtonGroup(String... buttonCaptions) {
		for (String buttonCaption : buttonCaptions) {
			addButton(buttonCaption);
		}
	}

	/**
	 * Creates a toggle buttons group with some default buttons.
	 * @param button	the default buttons to add to the group.
	 */
	public ToggleButtonGroup(Button... buttons) {
		for (Button button : buttons) {
			addButton(button);
		}
	}

	/**
	 * Add a listener to be notified when a selection change occur.
	 * @param listener	the listener.
	 */
	public void addListener(ToggleButtonGroupListener listener) {
		if (listeners == null) {
			listeners = new LinkedList<ToggleButtonGroupListener>();
		}

		listeners.add(listener);
	}

	/**
	 * Notify the listeners when the selection changes.
	 * @param selectedButtonIndex	the new selected button index.
	 * @param previousButtonIndex	the previous selected button index.
	 */
	protected void notifyListener(int selectedButtonIndex, int previousButtonIndex) {
		if (selectedButtonIndex == previousButtonIndex) {
			return;
		}

		if (listeners == null) {
			return;
		}

		ToggleButtonGroupEvent event = new ToggleButtonGroupEvent(this, selectedButtonIndex, previousButtonIndex);
		for (ToggleButtonGroupListener listener : listeners) {
			listener.selectedButtonChanged(event);
		}
	}

	/*
	 * The toggled style name.
	 */
	private final static String CSS_STYLE_TOGGLED = "toggled"; // "v-pressed";//

	/**
	 * Adds a button with the given caption to this button group. Group will be filled from left to right.
	 * @param buttonCaption	the button caption.
	 * @return	the button instance.
	 */
	public Button addButton(String buttonCaption) {
		return addButton(new Button(buttonCaption));
	}

	/* (non-Javadoc)
	 * @see org.vaadin.peter.buttongroup.ButtonGroup#addButton(com.vaadin.ui.Button)
	 */
	@Override
	public Button addButton(Button button) {
		button = super.addButton(button);

		// Make sure nothing bad happen. Or maybe this check is useless as anyway an exception will be thrown 
		// if the button don't get add. But then how can we know this except knowing what's in super, which is 
		// not reasonable. So we need to do our job here.
		if (button != null) {
			button.addClickListener(toggleClickListener);

			if (getComponentCount() == 0) {
				toggleButton(button);
			}

		}

		return button;
	}

	/*
	 * The current selected button index.
	 */
	private int currentSelectedButtonIndex = -1;

	/**
	 * Toggle the specified button.
	 * 
	 * FIXME: or name it setSelectedButton, or offer both APIs.
	 * 
	 * @param button	the button to toggle.
	 * @return	true if the button was toggled, otherwise false.
	 */
	public boolean toggleButton(Button button) {
		boolean toggleDone = false;
		int previousButtonIndex = currentSelectedButtonIndex;
		int selectedButtonIndex = -1;

		int i = 0;
		Iterator<Component> it = iterator();
		while (it.hasNext()) {
			Component nextButton = it.next();
			if (nextButton.equals(button)) {
				nextButton.addStyleName(CSS_STYLE_TOGGLED);

				selectedButtonIndex = i;
				toggleDone = true;

			} else {
				nextButton.removeStyleName(CSS_STYLE_TOGGLED);
			}
			i++;
		}

		toggleDone(toggleDone, selectedButtonIndex, previousButtonIndex);

		return toggleDone;
	}

	/**
	 * Toggle the button at the specified index.
	 * 
	 * FIXME: or name it setSelectedButtonIndex, or offer both APIs.
	 * 
	 * @param buttonIndex	the index of the button to toggle.
	 * @return	true if any button was toggled, otherwise false.
	 */
	public boolean toggleButtonIndex(int buttonIndex) {
		boolean toggleDone = false;
		int previousButtonIndex = currentSelectedButtonIndex;
		int selectedButtonIndex = -1;

		int i = 0;
		Iterator<Component> it = iterator();
		while (it.hasNext()) {
			if (i == buttonIndex) {
				it.next().addStyleName(CSS_STYLE_TOGGLED);

				selectedButtonIndex = i;
				toggleDone = true;

			} else {
				it.next().removeStyleName(CSS_STYLE_TOGGLED);
			}
			i++;
		}

		toggleDone(toggleDone, selectedButtonIndex, previousButtonIndex);

		return toggleDone;
	}

	/*
	 * Call when the toggle ends.
	 */
	private void toggleDone(boolean toggleDone, int selectedButtonIndex, int previousButtonIndex) {

		System.out.println("toggleDone " + toggleDone + ", " + selectedButtonIndex + ", " + previousButtonIndex);

		currentSelectedButtonIndex = selectedButtonIndex;

		System.out.println("currentSelectedButtonIndex: " + currentSelectedButtonIndex);

		if (toggleDone) {
			notifyListener(selectedButtonIndex, previousButtonIndex);
		}
	}

	/**
	 * Gets the current selected button index.
	 * @return	the current selected button index.
	 */
	public int getCurrentSelectedButtonIndex() {
		return currentSelectedButtonIndex;
	}

	/*
	 * Handle the toggle functionality when a button is clicked.
	 */
	class ToggleClickListener implements ClickListener {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			System.out.println("buttonClick currentSelectedButtonIndex: " + currentSelectedButtonIndex);

			toggleButton(event.getButton());
		}

	}

	/**
	 * Implement this to listen to the toggle button changes inside the ToggleButtonGroup.
	 */
	public static interface ToggleButtonGroupListener extends Serializable {

		/**
		 * Called when the ToggleButtonGroup selection changes.
		 * @param event	the event with the selection change details.
		 */
		void selectedButtonChanged(ToggleButtonGroupEvent event);

	}

	/**
	 * Event object for the toggled state change.
	 */
	public static class ToggleButtonGroupEvent extends Component.Event {

		// FIXME: 1. Should we add the button references? I think it's better with the index, because you can still
		// access the button through the ButtonGroup API, while for a quick implementation the index is more handy.
		// 2. Then should we use "new" for the newButtonIndex or only buttonIndex, same for "old" or to use "prev".

		/*
		 * The index of the current toggled button.
		 */
		private int selectedButtonIndex;

		/*
		 * The index of the old toggled button.
		 */
		private int previousButtonIndex;

		/**
		 * Create a toggled button event.
		 * @param source			the source of the event, a ToggleButtonGroup instance.
		 * @param selectedButtonIndex	the index of the current toggled button.
		 * @param previousButtonIndex	the index of the previously toggled button.
		 */
		public ToggleButtonGroupEvent(ToggleButtonGroup source, int selectedButtonIndex, int previousButtonIndex) {
			super(source);
			this.selectedButtonIndex = selectedButtonIndex;
			this.previousButtonIndex = previousButtonIndex;
		}

		/**
		 * Gets the source of the event, the ToggleButtonGroup with the selection changed.
		 * @return	the source ToggleButtonGroup.
		 */
		public ToggleButtonGroup getToggleButtonGroup() {
			return (ToggleButtonGroup) getSource(); // FIXME: This cast is not nice.
		}

		/**
		 * Gets the previous selected button index.
		 * @return	the previous selected button index.
		 */
		public int getPreviousButtonIndex() {
			return previousButtonIndex;
		}

		/**
		 * Gets the new selected button index.
		 * @return	the new selected button index.
		 */
		public int getSelectedButtonIndex() {
			return selectedButtonIndex;
		}

	}

}
