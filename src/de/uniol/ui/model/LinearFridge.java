package de.uniol.ui.model;


public class LinearFridge extends AbstractFridge {

	/** timestamp at which the last event occured */
	protected double lastActionTime = Double.NaN;

	public LinearFridge() {
		super("LinearFridge");
	}
	
	/*
	 * Events:
	 */

	public void doRun() {
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Be passive until necessary:
		if (t_current < t_max) {
			// Cool enough, start in warming phase immediately
			waitDelay(EV_BEGIN_WARMING, 0.0, t_max);
		} else {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0, t_min);
		}
	}
	
	public void doBeginCooling(Double t_dest) {
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = load;
		load = q_cooling;
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = tau(t_current, t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Remove any next scheduled warming event if present
		interrupt(EV_BEGIN_WARMING);
		// Delay next warming phase when cooling is finished
		waitDelay(EV_BEGIN_WARMING, timespan, t_max);
	}

	public void doBeginWarming(Double t_dest) {
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = load;
		load = q_warming;
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = tau(t_current, t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Remove any next scheduled cooling event if present
		interrupt(EV_BEGIN_COOLING);
		// Delay next cooling phase when warming is finished
		waitDelay(EV_BEGIN_COOLING, timespan, t_min);
	}
	
	/*
	 * Misc:
	 */

	protected void updateTemperature() {
		// Check simulation start phase
		if (Double.isNaN(lastActionTime)) {
			return;
		}
		// Calculate proper tau (time between simulation steps, one unit == one
		// hour)
		double tau = (getEventList().getSimTime() - lastActionTime) / 60.0;
		// Calculate proper epsilon
		eps = Math.exp(-(tau * a) / m_c);
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = (eps * t_current)
				+ ((1 - eps) * (t_surround - (eta * (load / a))));
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
	
	protected double tau(double t_from, double t_dest) {
		double dividend = t_dest - t_surround + (eta * (load / a));
		double divisor = t_from - t_surround + (eta * (load / a));
		double tau = -Math.log((dividend / divisor)) * (m_c / a);
		// Multiply by 60 because tau is calculated in hours, but simulation
		// uses minutes
		return tau * 60.0;
	}
}