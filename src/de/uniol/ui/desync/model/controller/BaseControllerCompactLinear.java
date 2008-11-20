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
package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;

/**
 * Parameterized version of the linear model. This implementation uses only one
 * event to model itself. This event schedules itself in predefined points in
 * time and updates load and temperature values on each occurence. This model
 * uses even less calculations than the linear version, while producing exactly
 * similar results.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */
public class BaseControllerCompactLinear extends AbstractController {

	/* event constants */ 
	public final static String EV_TARGET_TO = "TargetTo";
	
	public BaseControllerCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doRun() {
		// Check if we were set active externally
		if (fridge.isStartActive()) {
			// Target to min temp
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
		} else {
			// Fridge was not set active, start in passive mode:
			if (fridge.getT_current() < fridge.getT_max()) {
				// Update load
				fridge.setLoad(fridge.getQ_warming());
				// Target to max temp
				waitDelay(EV_TARGET_TO, 0.0, fridge.getT_max(), fridge
						.getQ_warming());
			} else {
				// Update load
				fridge.setLoad(fridge.getQ_cooling());
				// Target to min temp
				waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
						.getQ_cooling());
			}
		}
	}
	
	public void doTargetTo(Double t_dest, Double load) {
		// Calculate current temperature based on current load and elapsed time
		double t_current = fridge.updateTemperature();
		// Update load
		fridge.setLoad(load);
		// Calcuate time to reach t_dest
		double timespan = fridge.tau(t_current, t_dest, load);
		// Update action timestamp
		((LinearFridge) fridge).setLastActionTime(getEventList().getSimTime());
		// If we are cooling right now, begin warming next, and the other way,
		// respectively.
		double t_next = fridge.isActive() ? fridge.getT_max() : fridge
				.getT_min();
		double loadNext = fridge.isActive() ? fridge.getQ_warming() : fridge
				.getQ_cooling();
		// Delay next phase when t_dest will be reached
		waitDelay(EV_TARGET_TO, timespan, t_next, loadNext);
	}
}