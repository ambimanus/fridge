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
 * This controller extension adds the randomized damping mode to a
 * {@link DirectControllerCompactLinear}.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class RandomizedDirectCompactLinear extends
		DirectControllerCompactLinear implements IRandomized {
	
	public RandomizedDirectCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doLoadThermalStorage(Double spread) {
		double rnd = drawUniformRandom(0.0, spread);
		waitDelay(EV_DEL_AND_TARGET_TO, rnd, fridge.getT_min(), fridge
				.getQ_cooling());
		double cycle = fridge.tauCooling(fridge.getQ_cooling())
				+ fridge.tauWarming(fridge.getQ_warming());
		// TODO base cycle calculation on given load
		waitDelay(EV_RANDOMIZE_ACTION, rnd + cycle);
	}

	public void doUnloadThermalStorage(Double spread) {
		double rnd = drawUniformRandom(0.0, spread);
		waitDelay(EV_DEL_AND_TARGET_TO, rnd, fridge.getT_max(), fridge
				.getQ_warming());
		double cycle = fridge.tauCooling(fridge.getQ_cooling())
				+ fridge.tauWarming(fridge.getQ_warming());
		// TODO base cycle calculation on given load
		waitDelay(EV_RANDOMIZE_ACTION, rnd + cycle);
	}

	public void doRandomizeAction() {
		// Signal action has been performed, and we have reached our original
		// temperature after a full phase. Now randomize further behaviour by
		// targeting a random temperature in [Tmin,Tmax].
		double t_dest = drawUniformRandom(fridge.getT_min(), fridge.getT_max());
		if (t_dest > fridge.getT_current()) {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_warming());
		} else {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_cooling());
		}
	}
}