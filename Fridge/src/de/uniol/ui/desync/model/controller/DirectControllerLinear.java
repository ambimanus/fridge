package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Idsc;

public class DirectControllerLinear extends BaseControllerLinear implements
		Idsc {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";

	public DirectControllerLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_COOLING, drawUniformRandom(0.0, spread), fridge
				.getT_min(), fridge.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_WARMING, drawUniformRandom(0.0, spread), fridge
				.getT_max(), fridge.getQ_warming());
	}
}