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

import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

/**
 * This controller extension adds the randomized damping mode to a
 * {@link TimedControllerCompactLinear}.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class RandomizedTimedCompactLinear extends
		TimedControllerCompactLinear implements IRandomized {
	
	public RandomizedTimedCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		super.doReduceLoad(tau_preload, tau_reduce);
		waitDelay(EV_RANDOMIZE_ACTION, tau_preload + tau_reduce);
	}
	
	public void doRandomizeAction() {
		// Signal action has been performed, now randomize further behaviour by
		// targeting a random temperature in [Tmin,Tmax].
		double t_dest = drawUniformRandom(fridge.getT_min(), fridge.getT_max());
		if (t_dest > fridge.getT_current()) {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_warming());
		} else {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_cooling());
		}
	}
}