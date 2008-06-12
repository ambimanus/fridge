package de.uniol.ui.model;
import simkit.SimEntityBase;
import simkit.random.RandomVariate;

public class Fridge extends SimEntityBase {

	/** Runtime ID counter */
	private static int instance = 0;
	
	/* constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	public final static String EV_WARMING = "Warming";
	public final static String EV_COOLING = "Cooling";
	public final static String PROP_TEMPERATURE = "temperature";
	public final static String PROP_LOAD = "load";
	public final static double SIMULATION_CLOCK = 1.0;

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
	private double eps; // = Math.exp(-(tau * a) / m_c)
	/** inner temperature at previous timestamp */
	private double t_previous = Double.NaN;

	/* state variables */
	/** inner temperature at current timestamp */
	protected double t_current = 3.0;
	/** load at current timestamp */
	protected double load = 0.0;

	public Fridge() {
		setName("Fridge" + instance++);
		// Init default values
		initDefault();
	}
	
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
		tau = Fridge.SIMULATION_CLOCK / 60.0;
		// Init aux vars
		t_previous = Double.NaN;
		// Init state vars
		t_current = 3.0;
		load = 0.0;
	}

	public void doRun() {
		// Calculate proper epsilon
		eps = Math.exp(-(tau * a) / m_c);
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
		waitDelay(EV_COOLING, Fridge.SIMULATION_CLOCK);
	}
	
	public void doBeginWarming() {
		// Announce state change
		firePropertyChange(PROP_LOAD, q_cooling, q_warming);
		// Delay next state change
		waitDelay(EV_WARMING, Fridge.SIMULATION_CLOCK);
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
			waitDelay(EV_COOLING, Fridge.SIMULATION_CLOCK);
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
			waitDelay(EV_WARMING, Fridge.SIMULATION_CLOCK);
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