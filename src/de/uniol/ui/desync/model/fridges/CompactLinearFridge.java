package de.uniol.ui.desync.model.fridges;

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
		// Check if we were set active externally
		if (isStartActive()) {
			// Target to min temp
			waitDelay(EV_TARGET_TO, 0.0, t_min, q_cooling);
		} else {
			// Load is undefined, start in passive mode:
			if (t_current < t_max) {
				// Update load
				double bak = load;
				load = q_warming;
				// Announce load change
				firePropertyChange(PROP_LOAD, bak, load);
				// Target to max temp
				waitDelay(EV_TARGET_TO, 0.0, t_max, q_warming);
			} else {
				// Update load
				double bak = load;
				load = q_cooling;
				// Announce load change
				firePropertyChange(PROP_LOAD, bak, load);
				// Target to min temp
				waitDelay(EV_TARGET_TO, 0.0, t_min, q_cooling);
			}
		}
	}
	
	public void doTargetTo(Double t_dest, Double load) {
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = this.load;
		this.load = load;
		// Update active state
		active = load > q_warming;
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, this.load);
		// Calcuate time to reach t_dest
		double timespan = tau(t_current, t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Remove any next scheduled warming event if present
		interrupt(EV_TARGET_TO);
		// If we are cooling right now, begin warming next, and the other way,
		// respectively.
		double t_next = active ? t_max : t_min;
		double loadNext = active ? q_warming : q_cooling;
		// Delay next phase when t_dest will be reached
		waitDelay(EV_TARGET_TO, timespan, t_next, loadNext);
	}
}