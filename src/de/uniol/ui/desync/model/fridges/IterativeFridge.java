package de.uniol.ui.desync.model.fridges;

/**
 * Iterative implementation of a fridge. This model calculates temperature and
 * load values on each simulated time step.
 * 
 * @author Chh
 */
public class IterativeFridge extends AbstractFridge {

	/* event constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	public final static String EV_WARMING = "Warming";
	public final static String EV_COOLING = "Cooling";
	
	/** time between simulation steps (for equations, one unit == one hour) */
	protected double tau = SIMULATION_CLOCK / 60.0;

	/** system intertia, calculated value */
	protected double eps; // = Math.exp(-(tau * a) / m_c)
	/** whether the cooling device is active */
	protected boolean active = false;

	public IterativeFridge() {
		super("Fridge");
	}
	
	/*
	 * Events:
	 */

	public void doRun() {
		// Calculate proper epsilon
		eps = Math.exp(-(tau * a) / m_c);
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);

		// Check if we were set active externally
		if (isStartActive()) {
			// Target to min temp
			waitDelay(EV_BEGIN_COOLING, 0.0);
		} else {
			// Be passive until necessary:
			if (t_current < t_max) {
				// Cool enough, start in warming phase immediately
				waitDelay(EV_BEGIN_WARMING, 0.0);
			} else {
				// Too warm, start cooling immediately
				waitDelay(EV_BEGIN_COOLING, 0.0);
			}
		}
	}
	
	public void doBeginCooling() {
		// Update load
		double bak = load;
		load = q_cooling;
		// Announce state change
		active = true;
		firePropertyChange(PROP_LOAD, bak, load);
		// Delay next state change
		waitDelay(EV_COOLING, IterativeFridge.SIMULATION_CLOCK);
	}
	
	public void doBeginWarming() {
		// Update load
		double bak = load;
		load = q_warming;
		// Announce state change
		active = false;
		firePropertyChange(PROP_LOAD, bak, load);
		// Delay next state change
		waitDelay(EV_WARMING, IterativeFridge.SIMULATION_CLOCK);
	}

	public void doCooling() {
		// Calculate and announce new temperature
		updateTemperature();
		// Delay next state change
		if(t_current <= t_min) {
			// Cool enough, switch to 'warming' immediately
			waitDelay(EV_BEGIN_WARMING, 0.0);
		} else {
			// Desired temperature not reached, continue cooling
			waitDelay(EV_COOLING, IterativeFridge.SIMULATION_CLOCK);
		}
	}

	public void doWarming() {
		// Calculate and announce new temperature
		updateTemperature();
		// Delay next state change
		if (t_current >= t_max) {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0);
		} else {
			// Still cool enough, continue warming
			waitDelay(EV_WARMING, IterativeFridge.SIMULATION_CLOCK);
		}
	}
	
	public void initDefault() {
		super.initDefault();
		// Init tau
		tau = IterativeFridge.SIMULATION_CLOCK / 60.0;
	}
	
	/*
	 * Misc:
	 */

	/**
	 * Calculate new t_current based on the temperature at last time step and
	 * current load.
	 */
	protected void updateTemperature() {
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = (eps * t_current)
				+ ((1 - eps) * (t_surround - (eta * (load / a))));
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
	
	public boolean isActive() {
		return active;
	}
}