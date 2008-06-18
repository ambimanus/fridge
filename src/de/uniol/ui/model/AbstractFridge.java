package de.uniol.ui.model;

import simkit.SimEntityBase;
import simkit.random.RandomVariate;

public class AbstractFridge extends SimEntityBase {

	/** Runtime ID counter */
	protected static int instance = 0;
	
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
	protected double t_surround = 20.0;
	/** insulation */
	protected double a = 3.21;
	/** load in cooling phase */
	protected double q_cooling = 70.0;
	/** load in warming phase */
	protected double q_warming = 0.0;
	/** thermal mass */
	protected double m_c = 15.97;
	/** efficiency */
	protected double eta = 3.0;
	/** minimal inner temperature */
	protected double t_min = 3.0;
	/** maximal inner temperature */
	protected double t_max = 8.0;
	
	/* auxiliary variables */
	/** system intertia, calculated value */
	protected double eps; // = Math.exp(-(tau * a) / m_c)
	/** inner temperature at previous timestamp */
	protected double t_previous = Double.NaN;

	/* state variables */
	/** inner temperature at current timestamp */
	protected double t_current = t_min;
	/** load at current timestamp */
	protected double load = q_warming;
	
	public AbstractFridge(String nameBase) {
		setName(nameBase + instance++);
		// Init default values
		initDefault();
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
	
	/*
	 * Misc:
	 */
	
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