package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

public class StatefulDirectCompactLinearFullWidth extends DirectControllerCompactLinear
		implements IStateful {

	protected boolean active;
	protected boolean doUnload = false;
	
	public StatefulDirectCompactLinearFullWidth(LinearFridge fridge, int eventListID) {
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
		this.active = fridge.isActive();
		if ((!doUnload && !active) || (doUnload && active)) {
			// schedule RESTORE_STATE after a full cycle
			waitDelay(EV_RESTORE_STATE, fridge
					.tauCooling(fridge.getQ_cooling())
					+ fridge.tauWarming(fridge.getQ_warming()));
		}
		super.doDeleteAndTargetTo(t_dest, load);
	}

	public void doRestoreState() {
		if (doUnload && active) {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
		} else if (!doUnload && !active) {
			interruptAll(EV_TARGET_TO);
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_max(), fridge
					.getQ_warming());
		}
	}
}