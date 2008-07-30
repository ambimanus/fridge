package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class RandomizedDirectCompactLinear extends
		DirectControllerCompactLinear implements IRandomized {

	protected final static double PROPABILITY = 0.22;
	
	public RandomizedDirectCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doDeleteAndTargetTo(Double t_dest, Double load) {
		double cycle = fridge.tauCooling(fridge.getQ_cooling())
				+ fridge.tauWarming(fridge.getQ_warming());
		// TODO base cycle calculation on given load
		waitDelay(EV_RANDOMIZE_ACTION, cycle);
		super.doDeleteAndTargetTo(t_dest, load);
	}

	public void doRandomizeAction() {
		// Signal action has been performed, and we have reached our original
		// temperature after a full phase. Now randomize further behaviour, so
		// that we switch to cooling with a propability of 22%.
		if (drawBernoulli(PROPABILITY) == 1.0) {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
		} else {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_max(), fridge
					.getQ_warming());
		}
	}
}