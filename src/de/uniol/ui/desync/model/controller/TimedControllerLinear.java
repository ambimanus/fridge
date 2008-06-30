package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.strategies.IStrategyDirect;

public class TimedControllerLinear extends BaseControllerLinear implements
		IStrategyDirect {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";

	public TimedControllerLinear(LinearFridge fridge) {
		super(fridge);
	}
	
	public void doLoadThermalStorage(Double t_preload, Double spread) {
		waitDelay(EV_LOAD_THERMAL_STORAGE_NOW, t_preload
				+ drawUniformRandom(0.0, spread));
	}
	
	public void doUnloadThermalStorage(Double t_preload, Double spread) {
		waitDelay(EV_UNLOAD_THERMAL_STORAGE_NOW, t_preload
				+ drawUniformRandom(0.0, spread));
	}
	
	public void doLoadThermalStorageNow() {
		waitDelay(EV_BEGIN_COOLING, 0, fridge.getT_min());
	}

	public void doUnloadThermalStorageNow() {
		waitDelay(EV_BEGIN_WARMING, 0, fridge.getT_max());
	}
}