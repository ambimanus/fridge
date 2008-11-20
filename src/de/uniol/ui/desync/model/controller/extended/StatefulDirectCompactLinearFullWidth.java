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
 * This controller extension adds the stateful full-width damping mode to a
 * {@link DirectControllerCompactLinear}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
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