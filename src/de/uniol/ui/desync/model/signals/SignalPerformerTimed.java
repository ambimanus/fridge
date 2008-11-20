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
 * Signal performer which sends the TLR signal to controllers.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class SignalPerformerTimed extends AbstractSignalPerformer {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double tau_preload;
	private double tau_reduce;

	public SignalPerformerTimed(int eventListID, double t_notify, double tau_preload,
			double tau_reduce) {
		super(eventListID, "Signal: Timed Load Reduction");
		this.t_notify = t_notify;
		this.tau_preload = tau_preload;
		this.tau_reduce = tau_reduce;
	}

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Itlr)) {
			System.err.println("Wrong controller type for signal TIMED: \""
					+ c.getName() + '"');
			return;
//			throw new IllegalArgumentException("Wrong controller type: "
//					+ c.getName());
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		c.waitDelay(Itlr.EV_REDUCE_LOAD, 0, tau_preload,
				tau_reduce);
	}
}