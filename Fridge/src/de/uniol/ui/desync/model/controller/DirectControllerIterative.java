package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Idsc;

public class DirectControllerIterative extends BaseControllerIterative
		implements Idsc {
	
	public DirectControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_COOLING, drawUniformRandom(0.0, spread), fridge
				.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_WARMING, drawUniformRandom(0.0, spread), fridge
				.getQ_warming());
	}
}