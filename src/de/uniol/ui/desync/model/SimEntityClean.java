package de.uniol.ui.desync.model;

import simkit.BasicSimEntity;
import simkit.Priority;
import simkit.SimEntity;
import simkit.SimEntityBase;
import simkit.SimEvent;

/**
 * This implementation of {@link SimEntity} does not use Java Reflection to find
 * the apropriate method for an event and therefore is a lot faster than the
 * default implementation {@link SimEntityBase}.
 * 
 * @author Chh
 */
public abstract class SimEntityClean extends BasicSimEntity {
	
//	public SimEntityClean(String name, int eventListID) {
//		super(name, Priority.DEFAULT);
//		setEventListID(eventListID);
//	}
//
//	public SimEntityClean(int eventListID) {
//		this(DEFAULT_ENTITY_NAME, eventListID);
//	}

	protected final static String EV_RUN = "Run";
	protected final static String EV_DO_RUN = "doRun";

	public SimEntityClean(String name, int eventListID) {
		super(name, Priority.DEFAULT, eventListID);
	}

	public SimEntityClean(int eventListID) {
		super(DEFAULT_ENTITY_NAME, Priority.DEFAULT, eventListID);
	}

	@Override
	public void handleSimEvent(SimEvent event) {
		// Check for Run Event and execute it
		if (EV_RUN.equals(event.getEventName()) && this == event.getSource()) {
			doRun();
		} else {
			try {
				handleEvent(event);
			} catch (Exception e) {
				error(event, e);
			}
		}
	}

	public void processSimEvent(SimEvent event) {
		// The entities in this software are not intended to act as listeners to
		// SimEvents. Print a message to notify the developer about the ignored
		// event.
		error(event);
	}

	public abstract void doRun();

	public abstract void handleEvent(SimEvent event);
	
	/**
	 * Prints an error message about the given event that cannot be handled by
	 * this entity.
	 * 
	 * @param event
	 */
	protected void error(SimEvent event) {
		System.err.println(getName()
				+ " got a SimEvent which it cannot handle: " + event);
	}
	
	/**
	 * Prints an error message about the given event that cannot be handled by
	 * this entity. Appends the given Exception message.
	 * 
	 * @param event
	 */
	protected void error(SimEvent event, Exception e) {
		System.err.println(getName()
				+ " got a SimEvent which it cannot handle: " + event
				+ "\n\tCause: " + e.getMessage());
	}
}