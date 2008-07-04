package de.uniol.ui.desync.model.fridges;

public class LinearFridge extends AbstractFridge {
	
	/** timestamp at which the last event occured */
	protected double lastActionTime = Double.NaN;

	public LinearFridge() {
		super("LinearFridge");
	}
	
	protected LinearFridge(String name) {
		super(name);
	}
	
	/*
	 * Events:
	 */
	
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
	
//	public static double calculateTemperatureAfter(double elapsedTime,
//			double previousTemperature, AbstractFridge fridge, double load) {
//		// Calculate proper epsilon
//		double eps = Math.exp(-(elapsedTime * fridge.a) / fridge.m_c);
//		// Calculate temperature based on given load, elapsed time and
//		// previous temperature at (current time - elapsed time)
//		return (eps * previousTemperature)
//				+ ((1 - eps) * (fridge.t_surround - (fridge.eta * (load / fridge.a))));
//	}
	
	public double calculateTemperatureAfter(double elapsedTime,
			double previousTemperature, double load) {
		double ret;
		if (load > getQ_warming()) {
			if (Double.isNaN(tau_cooling)) {
				tau_cooling = tau(getT_max(), getT_min(), getQ_cooling());
			}
			ret = previousTemperature
					+ (((getT_min() - getT_max()) / tau_cooling) * elapsedTime);
		} else {
			if (Double.isNaN(tau_warming)) {
				tau_warming = tau(getT_min(), getT_max(), getQ_warming());
			}
			ret = previousTemperature
					+ (((getT_max() - getT_min()) / tau_warming) * elapsedTime);
		}
		return ret;
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
			if (Double.isNaN(tau_warming)) {
				tau_warming = tau(getT_min(), getT_max(), getQ_warming());
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_warming
			return ((t_dest - t_from) / range) * tau_warming;
		} else {
			// Calculate tau_cooling if not already done
			if (Double.isNaN(tau_cooling)) {
				tau_cooling = tau(getT_max(), getT_min(), getQ_cooling());
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_cooling
			return -((t_dest - t_from) / range) * tau_cooling;
		}
	}
}