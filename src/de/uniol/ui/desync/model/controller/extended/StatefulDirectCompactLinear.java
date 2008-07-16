package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class StatefulDirectCompactLinear extends DirectControllerCompactLinear {

	public final static String EV_RESTORE_STATE = "RestoreState";
	
	private double temp = Double.NaN;
	private boolean active;
	private boolean doUnload = false;
	
	public StatefulDirectCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doLoadThermalStorage(Double spread) {
		doUnload = false;
		super.doLoadThermalStorage(spread);
	}

	public void doUnloadThermalStorage(Double spread) {
		doUnload = true;
		super.doUnloadThermalStorage(spread);
	}

	public void doDeleteAndTargetTo(Double t_dest, Double load) {
		this.temp = fridge.getT_current();
		this.active = fridge.isActive();
		if (!doUnload && !active) {
			double time = ((LinearFridge) fridge).tau(temp, fridge.getT_min());
			double t_crossing = fridge.getT_min() + fridge.getT_max() - temp;
			time += ((LinearFridge) fridge).tau(fridge.getT_min(), t_crossing);
			waitDelay(EV_RESTORE_STATE, time);
		} else if (doUnload && active) {
			double time = ((LinearFridge) fridge).tau(temp, fridge.getT_max());
			double t_crossing = fridge.getT_min() + fridge.getT_max() - temp;
			time += ((LinearFridge) fridge).tau(fridge.getT_max(), t_crossing);
			waitDelay(EV_RESTORE_STATE, time);
		}
		super.doDeleteAndTargetTo(t_dest, load);
	}

	public void doRestoreState() {
		if (!doUnload && !active) {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
		} else if (doUnload && active) {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_max(), fridge
					.getQ_warming());
		}
	}
}