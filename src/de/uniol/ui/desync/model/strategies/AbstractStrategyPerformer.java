package de.uniol.ui.desync.model.strategies;

import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.controller.AbstractController;

public abstract class AbstractStrategyPerformer extends SimEntityClean {

	public final static String EV_APPLY_TO_CONTROLLER = "ApplyToController";
	
	public AbstractStrategyPerformer(int eventListID, String name) {
		super(eventListID);
		setName(name);
	}
	
	public abstract void doApplyToController(AbstractController c);
}