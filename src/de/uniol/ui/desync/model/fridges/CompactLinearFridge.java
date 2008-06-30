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
	
	public CompactLinearFridge() {
		super("CompactLinearFridge");
	}
	
	/*
	 * Events:
	 */

	public void doRun() {
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
}