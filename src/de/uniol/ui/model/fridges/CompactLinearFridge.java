package de.uniol.ui.model.fridges;

/**
 * Parameterized version of the linear model. This implementation uses only one
 * event to model itself. This event schedules itself in predefined points in
 * time and updates load and temperature values on each occurence. This model
 * uses even less calculations than the linear version, while producing exactly
 * similar results.
 * 
 * @author Chh
 */
public class CompactLinearFridge extends LinearFridge {

	/* event constants */ 
	public final static String EV_TARGET_TO = "TargetTo";
	
	public CompactLinearFridge() {
		super("CompactLinearFridge");
	}
	
	/*
	 * Events:
	 */

	public void doRun() {
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Be passive until necessary:
		if (t_current < t_max) {
			// Update load
			double bak = load;
			load = q_warming;
			// Announce load change
			firePropertyChange(PROP_LOAD, bak, load);
			// Target to max temp
			waitDelay(EV_TARGET_TO, 0.0, t_max);
		} else {
			// Update load
			double bak = load;
			load = q_cooling;
			// Announce load change
			firePropertyChange(PROP_LOAD, bak, load);
			// Target to min temp
			waitDelay(EV_TARGET_TO, 0.0, t_min);
		}
	}
	
	public void doTargetTo(Double t_dest) {
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = load;
		if (t_current < t_dest) {
			load = q_warming;
		} else {
			load = q_cooling;
		}
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, load);
		// Calcuate time to reach t_dest
		double timespan = tau(t_current, t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Remove any next scheduled warming event if present
		interrupt(EV_TARGET_TO);
		// If we are cooling right now, begin warming next, and the other way,
		// respectively.
		double t_next = (load == q_warming ? t_min : t_max);
		// Delay next phase when t_dest will be reached
		waitDelay(EV_TARGET_TO, timespan, t_next);
	}
}