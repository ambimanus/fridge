package de.uniol.ui.desync.model.signals;

/**
 * This interface defines the DSC control signal.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public interface Idsc {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";

	public abstract void doLoadThermalStorage(Double spread);

	public abstract void doUnloadThermalStorage(Double spread);
}