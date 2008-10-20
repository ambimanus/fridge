package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Idsc;

/**
 * This controller extension adds the DSC control mode to a
 * {@link BaseControllerLinear}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class DirectControllerLinear extends BaseControllerLinear implements
		Idsc {

	protected final static String EV_LOAD_THERMAL_STORAGE_NOW = "LoadThermalStorageNow";
	protected final static String EV_UNLOAD_THERMAL_STORAGE_NOW = "UnloadThermalStorageNow";
	protected final static String EV_DEL_AND_BEGIN_COOLING = "DeleteAndBeginCooling";
	protected final static String EV_DEL_AND_BEGIN_WARMING = "DeleteAndBeginWarming";

	public DirectControllerLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_DEL_AND_BEGIN_COOLING, drawUniformRandom(0.0, spread),
				fridge.getT_min(), fridge.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_DEL_AND_BEGIN_WARMING, drawUniformRandom(0.0, spread),
				fridge.getT_max(), fridge.getQ_warming());
	}
	
	public void doDeleteAndBeginCooling(Double t_dest, Double load) {
		// First perform cancelling edges
		interruptAll(EV_BEGIN_COOLING);
		interruptAll(EV_BEGIN_WARMING);
		// Then immediately perform requested operation
		waitDelay(EV_BEGIN_COOLING, 0.0, t_dest, load);
	}
	
	public void doDeleteAndBeginWarming(Double t_dest, Double load) {
		// First perform cancelling edges
		interruptAll(EV_BEGIN_COOLING);
		interruptAll(EV_BEGIN_WARMING);
		// Then immediately perform requested operation
		waitDelay(EV_BEGIN_WARMING, 0.0, t_dest, load);
	}
}