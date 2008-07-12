package de.uniol.ui.desync.model.controller;

import simkit.SimEvent;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Idsc;

public class DirectControllerIterative extends BaseControllerIterative
		implements Idsc {
	
	public DirectControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void handleEvent(SimEvent event) {
		if (EV_LOAD_THERMAL_STORAGE.equals(event.getEventName())) {
			doLoadThermalStorage((Double) event.getParameters()[0]);
		} else if (EV_UNLOAD_THERMAL_STORAGE.equals(event.getEventName())) {
			doUnloadThermalStorage((Double) event.getParameters()[0]);
		} else {
			super.handleEvent(event);
		}
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