package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class RandomizedTimedCompactLinear extends
		TimedControllerCompactLinear implements IRandomized {

	protected final static String EV_CALCULATE_ACTION = "CalculateAction";
	
	public RandomizedTimedCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		super.doReduceLoad(tau_preload, tau_reduce);
		waitDelay(EV_CALCULATE_ACTION, tau_preload);
	}
	
	public void doCalculateAction() {
		double timespan = fridge.tau(fridge.getT_current(), fridge.getT_max(),
				fridge.getQ_warming())
				+ fridge.tau(fridge.getT_max(), drawUniformRandom(fridge
						.getT_min(), fridge.getT_max()), fridge.getQ_cooling());
		waitDelay(EV_RANDOMIZE_ACTION, timespan);
	}

	public void doRandomizeAction() {
		waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge.getT_max(), fridge
				.getQ_warming());
	}
}