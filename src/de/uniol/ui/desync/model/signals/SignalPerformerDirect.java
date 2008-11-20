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

import de.uniol.ui.desync.model.controller.AbstractController;

/**
 * Signal performer which sends the DSC signal to controllers.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class SignalPerformerDirect extends AbstractSignalPerformer {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double spread;
	private boolean doUnload;

	public SignalPerformerDirect(int eventListID, double t_notify,
			double spread, boolean doUnload) {
		super(eventListID, "Signal: Direct Storage Control");
		this.t_notify = t_notify;
		this.spread = spread;
		this.doUnload = doUnload;
	}

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Idsc)) {
			System.err.println("Wrong controller type for signal DIRECT: '"
					+ c.getName() + '\'');
			return;
//			throw new IllegalArgumentException("Wrong controller type: "
//					+ c.getName());
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		String ev = doUnload ? Idsc.EV_UNLOAD_THERMAL_STORAGE
				: Idsc.EV_LOAD_THERMAL_STORAGE;
		c.waitDelay(ev, 0, spread);
	}
}