package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.controller.AbstractController;

public abstract class AbstractSignalPerformer extends SimEntityClean {

	public final static String EV_APPLY_TO_CONTROLLER = "ApplyToController";
	
	public AbstractSignalPerformer(int eventListID, String name) {
		super(eventListID);
		setName(name);
	}
	
	public void doRun() {
		// no-op
	}
	
	public abstract void doApplyToController(AbstractController c);
}