package de.uniol.ui.model;

import simkit.SimEntityBase;
import simkit.random.RandomVariate;

public class LinearFridge extends SimEntityBase {

	/** Runtime ID counter */
	private static int instance = 0;
	
	/* constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
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
	private double tau; // = SIMULATION_CLOCK * (1.0 / 60.0);
	/** system intertia, calculated value */
	private double eps; // = Math.exp(-(tau * a) / m_c)
	/** inner temperature at previous timestamp */
	private double t_previous = Double.NaN;
	/** timestamp at which the last event occured */
	private double lastActionTime = Double.NaN;

	/* state variables */
	/** inner temperature at current timestamp */
	protected double t_current = 3.0;
	/** load at current timestamp */
	protected double load = 0.0;

	public LinearFridge() {
		setName("LinearFridge" + instance++);
		// Init default values
		initDefault();
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
	
	public void doBeginCooling(Object... args) {
		// Get desired temperature
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected 1 argument, but got "
					+ args.length);
		}
		double t_dest = (Double) args[0];
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = load;
		load = q_cooling;
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = tauCooling(t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Delay next state change when cooling is finished
		waitDelay(EV_BEGIN_WARMING, timespan, t_max);
	}

	public void doBeginWarming(Object... args) {
		// Get desired temperature
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected 1 argument, but got "
					+ args.length);
		}
		double t_dest = (Double) args[0];
		// Calculate current temperature based on current load and elapsed time
		updateTemperature();
		// Update load
		double bak = load;
		load = q_warming;
		// Announce load change
		firePropertyChange(PROP_LOAD, bak, load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = tauWarming(t_dest);
		// Update action timestamp
		lastActionTime = getEventList().getSimTime();
		// Delay next state change when warming is finished
		waitDelay(EV_BEGIN_COOLING, timespan, t_min);
	}
	
	/*
	 * Parameter variance util methods:
	 */
	
	public void variateAllSequential(RandomVariate rv) {
		variate_a(rv);
		variate_eta(rv);
		variate_mC(rv);
		variate_qCooling(rv);
		variate_qWarming(rv);
		variate_tSurround(rv);
	}
	
	public void variateAllParallel(RandomVariate rv) {
		double var = rv.generate();
		a *= var;
		eta *= var;
		m_c *= var;
		q_cooling *= var;
		q_warming *= var;
		t_surround *= var;
	}
	
	public void variate_a(RandomVariate rv) {
		a *= rv.generate();
	}
	
	public void variate_eta(RandomVariate rv) {
		eta *= rv.generate();
	}
	
	public void variate_mC(RandomVariate rv) {
		m_c *= rv.generate();
	}
	
	public void variate_qCooling(RandomVariate rv) {
		q_cooling *= rv.generate();
	}
	
	public void variate_qWarming(RandomVariate rv) {
		q_warming *= rv.generate();
	}
	
	public void variate_tSurround(RandomVariate rv) {
		t_surround *= rv.generate();
	}
	
	public void generateAllSequential(RandomVariate rv) {
		generate_a(rv);
		generate_eta(rv);
		generate_mC(rv);
		generate_qCooling(rv);
		generate_qWarming(rv);
		generate_tSurround(rv);
	}
	
	public void generate_a(RandomVariate rv) {
		a = rv.generate();
	}
	
	public void generate_eta(RandomVariate rv) {
		eta = rv.generate();
	}
	
	public void generate_mC(RandomVariate rv) {
		m_c = rv.generate();
	}
	
	public void generate_qCooling(RandomVariate rv) {
		q_cooling = rv.generate();
	}
	
	public void generate_qWarming(RandomVariate rv) {
		q_warming = rv.generate();
	}
	
	public void generate_tSurround(RandomVariate rv) {
		t_surround = rv.generate();
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
		// Init aux vars
		t_previous = Double.NaN;
		// Init state vars
		t_current = t_min;
		load = Double.NaN;
	}
	
	/*
	 * Misc:
	 */

	private void updateTemperature() {
		// Calculate proper tau
		tau = (getEventList().getSimTime() - lastActionTime) / 60.0;
		// Calculate proper epsilon
		eps = Math.exp(-(tau * a) / m_c);
		// Update temperature in current phase defined by given load
		t_previous = t_current;
		t_current = (eps * t_current)
				+ ((1 - eps) * (t_surround - (eta * (load / a))));
		// Announce state change
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
	
	private double tauWarming(double t_dest) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private double tauCooling(double t_dest) {
		// TODO Auto-generated method stub
		return 0;
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