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
package de.uniol.ui.desync.model.fridges;

/**
 * Iterative implementation of a fridge. This follows the main equation of the
 * iterative model in the thesis.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class IterativeFridge extends AbstractFridge {
	
	/** time between simulation steps (for equations, one unit == one hour) */
	protected double tau = 1.0;

	/** system intertia, calculated value */
	protected double eps; // = Math.exp(-(tau * a) / m_c)

	public IterativeFridge(int eventListID) {
		super("IterativeFridge", eventListID);
	}
	
	/*
	 * Events:
	 */

	public void doRun() {
		super.doRun();
		// Calculate proper epsilon
		eps = Math.exp(-(tau / 60.0) * (a / m_c));
	}
	
	public void doStop() {
		// No operation needed, because this implementation of fridge updates
		// its state already each cycle and the STOP event will be fired with
		// lowest priority and therefore be executed after all other pending
		// events at this point in time.
	}
	
	/*
	 * Misc:
	 */
	
	public void initDefault() {
		super.initDefault();
		// Init tau
		tau = 1.0;
	}

	/**
	 * Calculate new t_current based on the temperature at last time step and
	 * current load.
	 */
	public double updateTemperature() {
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = calculateTemperatureAfter(isActive(), tau, t_current,
				getLoad());
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Range check
		if ((isActive() && t_previous < getT_min())
				|| (!isActive() && t_previous > getT_max())) {
			System.err.println(getEventList().getSimTime()
					+ " ERROR - out of range: " + getName()
					+ ".updateTemperature(previousTemperature=" + t_previous
					+ ", load=" + getLoad() + ") = " + t_current);
		}
		// Return
		return t_current;
	}
	
	public double calculateTemperatureAfter(boolean active, double elapsedTime,
			double previousTemperature, double load) {
		// Virtually simulate 'elapsedTime' time steps further from now on to
		// calculate the temperature how it would be
		double ret = previousTemperature;
		for (double i = tau; i <= elapsedTime; i += tau) {
			ret = (eps * ret)
					+ ((1 - eps) * (t_surround - (eta * (load / a))));
		}
		return ret;
	}

	/**
	 * @return the tau
	 */
	public double getTau() {
		return tau;
	}

	/**
	 * @param tau the tau to set
	 */
	public void setTau(double tau) {
		this.tau = tau;
	}
}