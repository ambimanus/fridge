package de.uniol.ui.desync.model.strategies;

import simkit.SimEntityBase;
import de.uniol.ui.desync.model.controller.AbstractController;

public abstract class AbstractStrategyPerformer extends SimEntityBase {

	public final static String EV_APPLY_TO_CONTROLLER = "ApplyToController";
	
	public AbstractStrategyPerformer(int eventListID, String name) {
		setEventListID(eventListID);
		setName(name);
	}
	
	public abstract void doApplyToController(AbstractController c);
}