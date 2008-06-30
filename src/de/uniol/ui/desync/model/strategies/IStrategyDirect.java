package de.uniol.ui.desync.model.strategies;

public interface IStrategyDirect {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";

	public abstract void doLoadThermalStorage(Double spread);

	public abstract void doUnloadThermalStorage(Double spread);
}