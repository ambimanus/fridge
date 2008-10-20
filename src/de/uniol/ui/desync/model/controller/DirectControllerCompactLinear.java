package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Idsc;

/**
 * This controller extension adds the DSC control mode to a
 * {@link BaseControllerCompactLinear}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class DirectControllerCompactLinear extends BaseControllerCompactLinear
		implements Idsc {

	protected final static String EV_DEL_AND_TARGET_TO = "DeleteAndTargetTo";
	
	public DirectControllerCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_DEL_AND_TARGET_TO, drawUniformRandom(0.0, spread), fridge
				.getT_min(), fridge.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_DEL_AND_TARGET_TO, drawUniformRandom(0.0, spread), fridge
				.getT_max(), fridge.getQ_warming());
	}
	
	public void doDeleteAndTargetTo(Double t_dest, Double load) {
		// First perform cancelling edge
		interruptAll(EV_TARGET_TO);
		// Then immediately perform requested operation
		waitDelay(EV_TARGET_TO, 0.0, t_dest, load);
	}
}