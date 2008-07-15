package de.uniol.ui.desync.model.fridges;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.uniol.ui.desync.util.MessagingEventList;


public class LinearFridge extends AbstractFridge {
	
	/** timestamp at which the last event occured */
	protected double lastActionTime = Double.NaN;
	/** defines whether to calculate temperatures */
	protected boolean announceTemperatures = true;
	/** interval in minutes in which the temperature will be announced */
	protected double announceInterval = 1.0;
	/** the listener which will be notified about simtime changes */
	protected PropertyChangeListener timeListener = null;

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

					public void propertyChange(PropertyChangeEvent evt) {
						Double time = (Double) evt.getNewValue();
						if (Double.isNaN(lastAnnounce)
								|| time >= lastAnnounce + announceInterval) {
							lastAnnounce = time;
							double newTemp = getT_current();
							LinearFridge f = LinearFridge.this;
							f.firePropertyChange(PROP_TEMPERATURE, newTemp);
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
		return calculateTemperatureAfter(tau, t_current, load);
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
		t_current = calculateTemperatureAfter(tau, t_previous, load);
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Return
		return t_current;
	}
	
	public double calculateTemperatureAfter(double elapsedTime,
			double previousTemperature, double load) {
		double ret;
		if (isActive()) {
			Double t_c = loadsToTauCooling.get(load);
			if (t_c == null) {
				t_c = tau(getT_max(), getT_min(), load);
				loadsToTauCooling.put(load, t_c);
			}
			ret = previousTemperature
					+ (((getT_min() - getT_max()) / t_c) * elapsedTime);
		} else {
			Double t_w = loadsToTauWarming.get(load);
			if (t_w == null) {
				t_w = tau(getT_min(), getT_max(), load);
				loadsToTauWarming.put(load, t_w);
			}
			ret = previousTemperature
					+ (((getT_max() - getT_min()) / t_w) * elapsedTime);
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
	public double tau(double t_from, double t_dest) {
		// Calculate range from t_min to t_max
		double range = getT_max() - getT_min();
		// Check direction: warming or cooling
		if (t_from < t_dest) {
			// Calculate tau_warming if not already done
			Double t_w = loadsToTauWarming.get(getQ_warming());
			if (t_w == null) {
				t_w = tau(getT_min(), getT_max(), getQ_warming());
				loadsToTauWarming.put(getQ_warming(), t_w);
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_warming
			return ((t_dest - t_from) / range) * t_w;
		} else {
			// Calculate tau_cooling if not already done
			Double t_c = loadsToTauCooling.get(getQ_cooling());
			if (t_c == null) {
				t_c = tau(getT_max(), getT_min(), getQ_cooling());
				loadsToTauCooling.put(getQ_cooling(), t_c);
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