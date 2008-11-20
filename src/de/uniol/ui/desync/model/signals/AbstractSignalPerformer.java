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
package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.controller.AbstractController;

/**
 * This abstract class defines a signal performer which sends a specific control
 * signal to a given controller. Thsi is a SimEntity.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public abstract class AbstractSignalPerformer extends SimEntityClean {

	public final static String EV_APPLY_TO_CONTROLLER = "ApplyToController";
	
	public AbstractSignalPerformer(int eventListID, String name) {
		super(eventListID);
		setName(name);
	}
	
	/**
	 * Event: Sends the contained signal to the given controller.
	 * @param c
	 */
	public abstract void doApplyToController(AbstractController c);
}