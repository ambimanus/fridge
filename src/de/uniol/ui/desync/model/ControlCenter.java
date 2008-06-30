package de.uniol.ui.desync.model;

import java.util.ArrayList;

import simkit.SimEntityBase;
import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.strategies.AbstractStrategyPerformer;

public class ControlCenter extends SimEntityBase {

	protected ArrayList<AbstractFridge> fridges;
	protected AbstractStrategyPerformer strategy;

	public ControlCenter(int eventListID, ArrayList<AbstractFridge> fridges,
			AbstractStrategyPerformer strategy) {
		setEventListID(eventListID);
		this.fridges = fridges;
		this.strategy = strategy;
	}

	public void doRun() {
		for (AbstractFridge f : fridges) {
			strategy.waitDelay(
					AbstractStrategyPerformer.EV_APPLY_TO_CONTROLLER, 0.0, f
							.getController());
		}
	}
}