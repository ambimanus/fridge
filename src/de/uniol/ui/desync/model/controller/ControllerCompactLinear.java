package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.CompactLinearFridge;

public class ControllerCompactLinear extends AbstractController {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";
	
	public ControllerCompactLinear(CompactLinearFridge fridge) {
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
		fridge.waitDelay(CompactLinearFridge.EV_TARGET_TO, 0, fridge
				.getT_min());
	}
	
	public void doUnloadThermalStorageNow() {
		fridge.waitDelay(CompactLinearFridge.EV_TARGET_TO, 0, fridge
				.getT_max());
	}
}