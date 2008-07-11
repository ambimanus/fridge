package de.uniol.ui.desync.model.signals;

public interface Idsc {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";

	public abstract void doLoadThermalStorage(Double spread);

	public abstract void doUnloadThermalStorage(Double spread);
}