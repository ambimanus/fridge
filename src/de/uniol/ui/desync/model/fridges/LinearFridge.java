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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.uniol.ui.desync.util.MessagingEventList;

/**
 * Linearized implementation of a fridge. This follows the equation of the
 * linear model in the thesis.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class LinearFridge extends AbstractFridge {
	
	/** timestamp at which the last event occured */
	protected double lastActionTime = Double.NaN;
	/** defines whether to calculate temperatures */
	protected boolean announceTemperatures = true;
	/** interval in minutes in which the temperature will be announced */
	protected double announceInterval = 1.0;
	/** the listener which will be notified about simtime changes */
	protected PropertyChangeListener timeListener = null;
	/** error check tolerance for timelistener */
	protected static double tolerance = 0.0001;

	public LinearFridge(int eventListID) {
		this("LinearFridge", eventListID);
	}
	
	protected LinearFridge(String name, int eventListID) {
		super(name, eventListID);
	}
	
	/*
	 * Events:
	 */
	
	public void doRun() {
		super.doRun();
		if (isAnnounceTemperatures()
				&& getEventList() instanceof MessagingEventList) {
			if (timeListener == null) {
				timeListener = new PropertyChangeListener() {
					private double lastAnnounce = Double.NaN;
					private double previousTemperature = Double.NaN;

					public void propertyChange(PropertyChangeEvent evt) {
						Double time = (Double) evt.getNewValue();
						if (Double.isNaN(lastAnnounce)
								|| time >= lastAnnounce + announceInterval) {
							lastAnnounce = time;
							double newTemp = getT_current();
							LinearFridge f = LinearFridge.this;
							f.firePropertyChange(PROP_TEMPERATURE, newTemp);
							// Range check
							if (newTemp < getT_min() - tolerance
									|| newTemp > getT_max() + tolerance) {
								System.err
										.println(getEventList().getSimTime()
												+ " ERROR - out of range: "
												+ getName()
												+ ".updateTemperature(previousTemperature="
												+ previousTemperature
												+ ", load=" + getLoad()
												+ ") = "
												+ newTemp);
							}
							previousTemperature = newTemp;
						}
					}
				};
			}
			MessagingEventList el = (MessagingEventList) getEventList();
			el.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME,
					timeListener);
		}
	}
	
	public void doStop() {
		// Calculate and update current temperature a last time before the
		// simulation stops.
		updateTemperature();
	}
	
	/*
	 * Overwritten to recalculate t_current, because this value is only
	 * calculated at state switches in the linear model and therefore not up to
	 * date in-between.
	 */
	public double getT_current() {
		// Check simulation start phase
		if (Double.isNaN(lastActionTime)) {
			return t_current;
		}
		// Calculate elapsed time since last state switch
		double tau = (getEventList().getSimTime() - lastActionTime);
		// Calculate current temperature based on temperature at last state
		// switch and elapsed time since
		return calculateTemperatureAfter(isActive(), tau, t_current, getLoad());
	}
	
	/*
	 * Misc:
	 */

	/**
	 * Calculate new t_current based on current load and elapsed time since last
	 * call.
	 */
	public double updateTemperature() {
		// Check simulation start phase
		if (Double.isNaN(lastActionTime)) {
			lastActionTime = getEventList().getSimTime();
			return t_current;
		}
		// Calculate proper tau (time between actions, one unit == one
		// SIMULATION_STEP)
		double tau = (getEventList().getSimTime() - lastActionTime);
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = calculateTemperatureAfter(isActive(), tau, t_previous,
				getLoad());
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Return
		return t_current;
	}
	
	public double calculateTemperatureAfter(boolean active, double elapsedTime,
			double previousTemperature, double load) {
		double ret;
		if (active) {
			ret = previousTemperature
					+ (((getT_min() - getT_max()) / tauCooling(load)) * elapsedTime);
		} else {
			ret = previousTemperature
					+ (((getT_max() - getT_min()) / tauWarming(load)) * elapsedTime);
		}
		return ret;
	}
	
	/**
	 * Calculate time needed to reach temperature <code>t_dest</code>,
	 * starting at temperature <code>t_from</code>. Calculation is based on
	 * linear approximation by using precalculated tau_cooling or tau_warming.
	 * <p>
	 * Warning: tau_cooling and tau_warming are based on q_cooling and q_warming
	 * respectively. So this method depends on these values, too. If another
	 * load should be assumed, use method
	 * <code>tau(double t_from, double t_dest, double load)</code>, which is
	 * a little bit slower in calculation, but more precise and flexible.
	 * 
	 * @param t_from
	 * @param t_dest
	 * @return
	 */
	public double tau(double t_from, double t_dest, double load) {
		// Calculate range from t_min to t_max
		double range = getT_max() - getT_min();
		// Check direction: warming or cooling
		if (t_from < t_dest) {
			// Calculate tau_warming if not already done
			Double t_w = loadsToTauWarming.get(load);
			if (t_w == null) {
				t_w = super.tau(getT_min(), getT_max(), load);
				loadsToTauWarming.put(load, t_w);
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_warming
			return ((t_dest - t_from) / range) * t_w;
		} else {
			// Calculate tau_cooling if not already done
			Double t_c = loadsToTauCooling.get(load);
			if (t_c == null) {
				t_c = super.tau(getT_max(), getT_min(), load);
				loadsToTauCooling.put(load, t_c);
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_cooling
			return -((t_dest - t_from) / range) * t_c;
		}
	}

	/**
	 * @return the lastActionTime
	 */
	public double getLastActionTime() {
		return lastActionTime;
	}

	/**
	 * @param lastActionTime the lastActionTime to set
	 */
	public void setLastActionTime(double lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	/**
	 * @return the announceTemperatures
	 */
	public boolean isAnnounceTemperatures() {
		return this.announceTemperatures;
	}

	/**
	 * @param announceTemperatures the announceTemperatures to set
	 */
	public void setAnnounceTemperatures(boolean announceTemperatures) {
		this.announceTemperatures = announceTemperatures;
		if (!isAnnounceTemperatures() && timeListener != null) {
			MessagingEventList el = (MessagingEventList) getEventList();
			el.removePropertyChangeListener(PROP_TEMPERATURE, timeListener);
		}
	}

	/**
	 * @return the announceInterval
	 */
	public double getAnnounceInterval() {
		return announceInterval;
	}

	/**
	 * @param announceInterval the announceInterval to set
	 */
	public void setAnnounceInterval(double announceInterval) {
		this.announceInterval = announceInterval;
	}
}