package de.uniol.ui.desync.model.fridges;


/**
 * Linear implementation of a fridge. This model calculates temperature and load
 * values only on phase changes: First a desired temperature is defined. The
 * model then calculates the time needed to reach this temperature. An event is
 * then scheduled at the calculated point in time. When this event is processed,
 * it schedules another event to reach a newly defined target temperature. Using
 * this paradigm, the model easily switches warming/cooling phases with very
 * little calculations needed.
 * 
 * @author Chh
 */
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

	public void doRun() {
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
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
		return calculateTemperatureAfter(tau, t_current, this, load);
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
		t_current = calculateTemperatureAfter(tau, t_previous, this, load);
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
	
	public static double calculateTemperatureAfter(double elapsedTime,
			double previousTemperature, AbstractFridge fridge, double load) {
		double ret;
		if (load > fridge.getQ_warming()) {
			if (Double.isNaN(fridge.tau_cooling)) {
				fridge.tau_cooling = fridge.tau(fridge.getT_max(), fridge
						.getT_min(), fridge.getQ_cooling());
			}
			ret = previousTemperature
					+ (((fridge.getT_min() - fridge.getT_max()) / fridge.tau_cooling) * elapsedTime);
		} else {
			if (Double.isNaN(fridge.tau_warming)) {
				fridge.tau_warming = fridge.tau(fridge.getT_min(), fridge
						.getT_max(), fridge.getQ_warming());
			}
			ret = previousTemperature
					+ (((fridge.getT_max() - fridge.getT_min()) / fridge.tau_warming) * elapsedTime);
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
}