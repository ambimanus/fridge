package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;

public class DirectControllerIterative extends DirectAbstractController {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";
	
	public DirectControllerIterative(IterativeFridge fridge) {
		super(fridge);
		setName("DirectStorageController for " + fridge.getName());
	}
	
	public void doRun() {
		// no-op
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
		fridge.waitDelay(IterativeFridge.EV_BEGIN_COOLING, 0);
	}
	
	public void doUnloadThermalStorageNow() {
		fridge.waitDelay(IterativeFridge.EV_BEGIN_WARMING, 0);
	}
}