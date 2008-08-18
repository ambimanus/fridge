package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class RandomizedDirectCompactLinear extends
		DirectControllerCompactLinear implements IRandomized {
	
	public RandomizedDirectCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doLoadThermalStorage(Double spread) {
		double rnd = drawUniformRandom(0.0, spread);
		waitDelay(EV_DEL_AND_TARGET_TO, rnd, fridge.getT_min(), fridge
				.getQ_cooling());
		double cycle = fridge.tauCooling(fridge.getQ_cooling())
				+ fridge.tauWarming(fridge.getQ_warming());
		// TODO base cycle calculation on given load
		waitDelay(EV_RANDOMIZE_ACTION, rnd + cycle);
	}

	public void doUnloadThermalStorage(Double spread) {
		double rnd = drawUniformRandom(0.0, spread);
		waitDelay(EV_DEL_AND_TARGET_TO, rnd, fridge.getT_max(), fridge
				.getQ_warming());
		double cycle = fridge.tauCooling(fridge.getQ_cooling())
				+ fridge.tauWarming(fridge.getQ_warming());
		// TODO base cycle calculation on given load
		waitDelay(EV_RANDOMIZE_ACTION, rnd + cycle);
	}

	public void doRandomizeAction() {
		// Signal action has been performed, and we have reached our original
		// temperature after a full phase. Now randomize further behaviour by
		// targeting a random temperature in [Tmin,Tmax].
		double t_dest = drawUniformRandom(fridge.getT_min(), fridge.getT_max());
		if (t_dest > fridge.getT_current()) {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_warming());
		} else {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_cooling());
		}
	}
}