/*
 * AdaptiveFridge Copyright (C) 2008 Christian Hinrichs
 * 
 * AdaptiveFridge is copyright under the GNU General Public License.
 * 
 * This file is part of AdaptiveFridge.
 * 
 * AdaptiveFridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AdaptiveFridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AdaptiveFridge.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

/**
 * This controller extension adds the stateful half-width damping mode to a
 * {@link DirectControllerCompactLinear}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class StatefulDirectCompactLinearHalfWidth extends DirectControllerCompactLinear
		implements IStateful {

	protected boolean active;
	protected boolean doUnload = false;
	
	public StatefulDirectCompactLinearHalfWidth(LinearFridge fridge, int eventListID) {
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
		if (!doUnload && !active) {
			double temp = fridge.getT_current();
			// T_crossing = temperature where regular and modified will cross
			// the first time
			double T_crossing = fridge.getT_min() + fridge.getT_max() - temp;
			// timespan to this first crossing = time to cool down to T_min ...
			double time = fridge.tau(temp, fridge.getT_min(), fridge
					.getQ_cooling())
					// ... + time to warm up to T_crossing
					+ fridge.tau(fridge.getT_min(), T_crossing, fridge
							.getQ_warming());
			// schedule RESTORE_STATE at this future point in time
			waitDelay(EV_RESTORE_STATE, time);
		} else if (doUnload && active) {
			double temp = fridge.getT_current();
			// T_crossing = temperature where regular and modified will cross
			// the first time
			double T_crossing = fridge.getT_min() + fridge.getT_max() - temp;
			// timespan to this first crossing = time to warm up to T_max ...
			double time = fridge.tau(temp, fridge.getT_max(), fridge
					.getQ_warming())
					// ... + time to cool down to T_crossing
					+ fridge.tau(fridge.getT_max(), T_crossing, fridge
							.getQ_cooling());
			// schedule RESTORE_STATE at this future point in time
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