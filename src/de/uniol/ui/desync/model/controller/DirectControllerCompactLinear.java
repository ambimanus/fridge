package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Idsc;

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