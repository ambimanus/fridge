package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.strategies.IStrategyDirect;

public class TimedControllerIterative extends BaseControllerIterative implements
		IStrategyDirect {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";

	public TimedControllerIterative(IterativeFridge fridge) {
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
		waitDelay(EV_BEGIN_COOLING, 0, fridge.getQ_cooling());
	}
	
	public void doUnloadThermalStorageNow() {
		waitDelay(EV_BEGIN_WARMING, 0, fridge.getQ_warming());
	}
}