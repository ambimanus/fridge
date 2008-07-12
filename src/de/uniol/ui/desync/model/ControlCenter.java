package de.uniol.ui.desync.model;

import java.util.ArrayList;

import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.signals.AbstractSignalPerformer;

public class ControlCenter extends SimEntityClean {

	protected ArrayList<AbstractFridge> fridges;
	protected AbstractSignalPerformer strategy;

	public ControlCenter(int eventListID, ArrayList<AbstractFridge> fridges,
			AbstractSignalPerformer strategy) {
		super(eventListID);
		this.fridges = fridges;
		this.strategy = strategy;
	}

	public void doRun() {
		for (AbstractFridge f : fridges) {
			strategy.waitDelay(
					AbstractSignalPerformer.EV_APPLY_TO_CONTROLLER, 0.0, f
							.getController());
		}
	}
}