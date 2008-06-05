package de.uniol.ui.model;
import simkit.SimEntityBase;
import de.uniol.ui.desync.util.VarianceGenerator;

public class Fridge extends SimEntityBase {

	/* constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	public final static String EV_WARMING = "Warming";
	public final static String EV_COOLING = "Cooling";
	public final static String PROP_TEMPERATURE = "temperature";
	public final static String PROP_LOAD = "load";

	/* device parameters */
	/** surrounding temperature */
	private double t_surround = 20.0;
	/** insulation */
	private double a = 3.21;
	/** load in cooling phase */
	private double q_cooling = 70.0;
	/** load in warming phase */
	private double q_warming = 0.0;
	/** thermal mass */
	private double m_c = 15.97;
	/** efficiency */
	private double eta = 3.0;
	/** minimal inner temperature */
	private double t_min = 3.0;
	/** maximal inner temperature */
	private double t_max = 8.0;

	/* auxiliary variables */
	/** time between simulation steps (for equations, one unit == one hour) */
	private double tau = 1.0 / 60.0;
	/** system intertia, calculated value */
	private double eps = Math.exp(-(tau * a) / m_c);
	/** inner temperature at previous timestamp */
	private double t_previous = Double.NaN;

	/* state variables */
	/** inner temperature at current timestamp */
	protected double t_current = 3.0;
	/** load at current timestamp */
	protected double load = 0.0;

	public Fridge(VarianceGenerator vg) {
		// Init default values
		initDefault();
		if (vg != null) {
			// Deviate device parameters
			t_surround *= vg.generate();
			a *= vg.generate();
			q_cooling *= vg.generate();
			q_warming *= vg.generate();
			m_c *= vg.generate();
			eta *= vg.generate();
		}
	}
	
	public void initDefault() {
		// Init parameters
		t_surround = 20.0;
		a = 3.21;
		q_cooling = 70.0;
		q_warming = 0.0;
		m_c = 15.97;
		eta = 3.0;
		t_min = 3.0;
		t_max = 8.0;
		tau = 1.0 / 60.0;
		// Init aux vars
		eps = Math.exp(-(tau * a) / m_c);
		t_previous = Double.NaN;
		// Init state vars
		t_current = 3.0;
		load = 0.0;
	}

	public void doRun() {
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
		// Be passive until necessary:
		if (t_current < t_max) {
			// Cool enough, start in warming phase immediately
			waitDelay(EV_BEGIN_WARMING, 0);
		} else {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0);
		}
	}
	
	public void doBeginCooling() {
		// Announce state change
		firePropertyChange(PROP_LOAD, q_warming, q_cooling);
		// Delay next state change
		waitDelay(EV_COOLING, 1.0);
	}
	
	public void doBeginWarming() {
		// Announce state change
		firePropertyChange(PROP_LOAD, q_cooling, q_warming);
		// Delay next state change
		waitDelay(EV_WARMING, 1.0);
	}

	public void doCooling() {
		// Calculate and announce new temperature
		updateTemperature(q_cooling);
		// Delay next state change
		if(t_current <= t_min) {
			// Cool enough, switch to 'warming' immediately
			waitDelay(EV_BEGIN_WARMING, 0.0);
		} else {
			// Desired temperature not reached, continue cooling
			waitDelay(EV_COOLING, 1.0);
		}
	}

	public void doWarming() {
		// Calculate and announce new temperature
		updateTemperature(q_warming);
		// Delay next state change
		if (t_current >= t_max) {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0);
		} else {
			// Still cool enough, continue warming
			waitDelay(EV_WARMING, 1.0);
		}
	}

	private void updateTemperature(double load) {
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = (eps * t_current)
				+ ((1 - eps) * (t_surround - (eta * (load / a))));
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
	
	public String toString() {
		String out = super.toString();
		out += "(";
		out += "t_surround=" + t_surround;
		out += ", a=" + a;
		out += ", q_cooling=" + q_cooling;
		out += ", q_warming=" + q_warming;
		out += ", m_c=" + m_c;
		out += ", eta=" + eta;
		out += ")";
		return out;
	}
}