package de.uniol.ui.desync.model.fridges;

public class IterativeFridge extends AbstractFridge {
	
	/** time between simulation steps (for equations, one unit == one hour) */
	protected double tau = SIMULATION_CLOCK / 60.0;

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
		eps = Math.exp(-(tau * a) / m_c);
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
		tau = IterativeFridge.SIMULATION_CLOCK / 60.0;
	}

	/**
	 * Calculate new t_current based on the temperature at last time step and
	 * current load.
	 */
	public double updateTemperature() {
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = (eps * t_current)
				+ ((1 - eps) * (t_surround - (eta * (load / a))));
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Return
		return t_current;
	}
}