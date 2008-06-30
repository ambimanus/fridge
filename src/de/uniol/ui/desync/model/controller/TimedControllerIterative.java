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
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_COOLING, drawUniformRandom(0.0, spread), fridge
				.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_WARMING, drawUniformRandom(0.0, spread), fridge
				.getQ_warming());
	}
}