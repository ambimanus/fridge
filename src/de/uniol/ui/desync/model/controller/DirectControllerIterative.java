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

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Idsc;

/**
 * This controller extension adds the DSC control mode to a
 * {@link BaseControllerIterative}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class DirectControllerIterative extends BaseControllerIterative
		implements Idsc {
	
	public DirectControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doLoadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_COOLING, drawUniformRandom(0.0, spread), fridge
				.getQ_cooling());
	}
	
	public void doUnloadThermalStorage(Double spread) {
		waitDelay(EV_BEGIN_WARMING, drawUniformRandom(0.0, spread), fridge
				.getQ_warming());
	}

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginCooling(java.lang.Double)
	 */
	@Override
	public void doBeginCooling(Double load) {
		// First perform cancelling edges
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		// Then immediately perform requested operation
		super.doBeginCooling(load);
	}

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginWarming(java.lang.Double)
	 */
	@Override
	public void doBeginWarming(Double load) {
		// First perform cancelling edges
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		// Then immediately perform requested operation
		super.doBeginWarming(load);
	}
}