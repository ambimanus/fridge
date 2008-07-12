package de.uniol.ui.desync.model.controller;

import simkit.SimEvent;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Idsc;

public class DirectControllerCompactLinear extends BaseControllerCompactLinear
		implements Idsc {
	
	public DirectControllerCompactLinear(LinearFridge fridge, int eventListID) {
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
		waitDelay(EV_TARGET_TO, drawUniformRandom(0.0, spread), fridge
				.getT_min(), fridge.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_TARGET_TO, drawUniformRandom(0.0, spread), fridge
				.getT_max(), fridge.getQ_warming());
	}
}