package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;

public class DirectControllerLinear extends DirectAbstractController {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";
	
	public DirectControllerLinear(LinearFridge fridge) {
		super(fridge);
		setName("DirectStorageController for " + fridge.getName());
	}
	
	public void doRun() {
		// no-op
	}
	
	public void doLoadThermalStorage(Double tau_preload, Double spread) {
		waitDelay(EV_LOAD_THERMAL_STORAGE_NOW, tau_preload
				+ drawUniformRandom(0.0, spread));
	}
	
	public void doUnloadThermalStorage(Double tau_preload, Double spread) {
		waitDelay(EV_UNLOAD_THERMAL_STORAGE_NOW, tau_preload
				+ drawUniformRandom(0.0, spread));
	}
	
	public void doLoadThermalStorageNow() {
		fridge.waitDelay(LinearFridge.EV_BEGIN_COOLING, 0, fridge.getT_min());
	}
	
	public void doUnloadThermalStorageNow() {
		fridge.waitDelay(LinearFridge.EV_BEGIN_WARMING, 0, fridge.getT_max());
	}
}