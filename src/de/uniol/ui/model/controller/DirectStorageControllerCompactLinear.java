package de.uniol.ui.model.controller;

import simkit.SimEntityBase;
import de.uniol.ui.model.fridges.CompactLinearFridge;

public class DirectStorageControllerCompactLinear extends SimEntityBase {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";
	
	protected final static String EV_PERFORM_LOAD_THERMAL_STORAGE = "PerformLoadThermalStorage";
	protected final static String EV_PERFORM_UNLOAD_THERMAL_STORAGE = "PerformUnloadThermalStorage";
	
	private CompactLinearFridge fridge;
	
	public DirectStorageControllerCompactLinear(CompactLinearFridge fridge) {
		this.fridge = fridge;
		setName("Controller for " + fridge.getName());
	}
	
	public void doRun() {
		// no-op
	}
	
	public void doLoadThermalStorage(Double t_preload) {
		waitDelay(EV_PERFORM_LOAD_THERMAL_STORAGE, t_preload);
	}
	
	public void doUnloadThermalStorage(Double t_preload) {
		waitDelay(EV_PERFORM_UNLOAD_THERMAL_STORAGE, t_preload);
	}
	
	public void doPerformLoadThermalStorage() {
		fridge.waitDelay(CompactLinearFridge.EV_TARGET_TO, 0, fridge
				.getT_min());
	}
	
	public void doPerformUnloadThermalStorage() {
		fridge.waitDelay(CompactLinearFridge.EV_TARGET_TO, 0, fridge
				.getT_max());
	}
}