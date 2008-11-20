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
package de.uniol.ui.desync.model;

import java.util.ArrayList;

import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.signals.AbstractSignalPerformer;

/**
 * This class represents a control center which produces the control signals and
 * sends them to the controllers. This is modelled as SimEntity.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class ControlCenter extends SimEntityClean {

	protected ArrayList<AbstractFridge> fridges;
	protected AbstractSignalPerformer strategy;

	public ControlCenter(int eventListID, ArrayList<AbstractFridge> fridges,
			AbstractSignalPerformer strategy) {
		super(eventListID);
		this.fridges = fridges;
		this.strategy = strategy;
	}

	public void doRun() {
		for (AbstractFridge f : fridges) {
			strategy.waitDelay(
					AbstractSignalPerformer.EV_APPLY_TO_CONTROLLER, 0.0, f
							.getController());
		}
	}
}