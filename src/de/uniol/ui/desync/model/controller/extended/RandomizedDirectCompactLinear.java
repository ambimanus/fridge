package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class RandomizedDirectCompactLinear extends
		DirectControllerCompactLinear implements IRandomized {

	protected final static double PROPABILITY = 0.22;
	
	protected boolean doUnload = false;
	
	public RandomizedDirectCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doLoadThermalStorage(Double spread) {
		doUnload = false;
		super.doLoadThermalStorage(spread);
	}

	public void doUnloadThermalStorage(Double spread) {
		doUnload = true;
		super.doUnloadThermalStorage(spread);
	}

	@Override
	public void doDeleteAndTargetTo(Double t_dest, Double load) {
		if (doUnload && fridge.isActive()) {
			double quarter = (fridge.getT_max() - fridge.getT_min()) / 4;
			double rnd = drawUniformRandom(fridge.getT_min() + quarter, fridge
					.getT_max()
					- quarter);
			waitDelay(EV_RANDOMIZE_ACTION, fridge.tau(fridge.getT_current(),
					t_dest, load)
					+ fridge.tauCooling(fridge.getQ_cooling())
					+ fridge.tau(fridge.getT_min(), rnd, fridge.getQ_warming()));
		} else if (!doUnload && !fridge.isActive()) {
			double quarter = (fridge.getT_max() - fridge.getT_min()) / 4;
			double rnd = drawUniformRandom(fridge.getT_min() + quarter, fridge
					.getT_max()
					- quarter);
			waitDelay(EV_RANDOMIZE_ACTION, fridge.tau(fridge.getT_current(),
					t_dest, load)
					+ fridge.tauWarming(fridge.getQ_warming())
					+ fridge.tau(fridge.getT_max(), rnd, fridge.getQ_cooling()));
		}
		super.doDeleteAndTargetTo(t_dest, load);
	}

	public void doRandomizeAction() {
		// Signal action has been performed, and the phase after that signal is
		// completed by 50%. Now randomize further behaviour, so that we switch
		// to cooling with a propability of 22%.
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