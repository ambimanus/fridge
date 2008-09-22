package de.uniol.ui.desync.model.fridges;

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