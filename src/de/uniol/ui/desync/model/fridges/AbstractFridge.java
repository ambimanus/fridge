package de.uniol.ui.desync.model.fridges;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import simkit.SimEntityBase;
import simkit.random.RandomVariate;
import de.uniol.ui.desync.model.controller.AbstractController;
import de.uniol.ui.desync.util.MessagingEventList;

/**
 * This is the abstract base class for fridge entites. It containes the base
 * model parameters as well as methods to distribute the values based on random
 * variates.
 * 
 * @author Chh
 */
public abstract class AbstractFridge extends SimEntityBase {

	/** Runtime ID counter */
	protected static int instance = 0;
	
	/* constants */
	public final static String PROP_TEMPERATURE = "temperature";
	public final static String PROP_LOAD = "load";
	public final static double SIMULATION_CLOCK = 1.0;
	/** surrounding temperature */
	public final static double DEFAULT_t_surround = 20.0;
	/** insulation */
	public final static double DEFAULT_a = 3.21;
	/** load in cooling phase */
	public final static double DEFAULT_q_cooling = 70.0;
	/** load in warming phase */
	public final static double DEFAULT_q_warming = 0.0;
	/** thermal mass */
	public final static double DEFAULT_m_c = 19.95;
	/** efficiency */
	public final static double DEFAULT_eta = 3.0;
	/** minimal inner temperature */
	public final static double DEFAULT_t_min = 3.0;
	/** maximal inner temperature */
	public final static double DEFAULT_t_max = 8.0;

	/* device parameters */
	/** surrounding temperature */
	protected double t_surround = DEFAULT_t_surround;
	/** insulation */
	protected double a = DEFAULT_a;
	/** load in cooling phase */
	protected double q_cooling = DEFAULT_q_cooling;
	/** load in warming phase */
	protected double q_warming = DEFAULT_q_warming;
	/** thermal mass */
	protected double m_c = DEFAULT_m_c;
	/** efficiency */
	protected double eta = DEFAULT_eta;
	/** minimal inner temperature */
	protected double t_min = DEFAULT_t_min;
	/** maximal inner temperature */
	protected double t_max = DEFAULT_t_max;
	
	/* auxiliary variables */
	/** inner temperature at previous timestamp */
	protected double t_previous = Double.NaN;
	/** whether this device starts in state 'cooling' */
	protected boolean startActive = false;
	/** time to cooldown from t_max to t_min */
	protected double tau_cooling = Double.NaN;
	/** time to warmup from t_min to t_max */
	protected double tau_warming = Double.NaN;

	/* state variables */
	/** inner temperature at current timestamp */
	protected double t_current = t_min;
	/** load at current timestamp */
	protected double load = Double.NaN;
	
	/* Components */
	/** Controller which controls this device */
	protected AbstractController controller = null;
	
	/**
	 * Creates a new fridge entity with the given name. An instance counter will
	 * be added to the name for identification purposes.
	 * 
	 * @param nameBase
	 */
	public AbstractFridge(String nameBase) {
		setName(nameBase + instance++);
		// Init default values
		initDefault();
	}
	
	/**
	 * Sets the device parameters to the default.
	 */
	public void initDefault() {
		// Init parameters
		t_surround = DEFAULT_t_surround;
		a = DEFAULT_a;
		q_cooling = DEFAULT_q_cooling;
		q_warming = DEFAULT_q_warming;
		m_c = DEFAULT_m_c;
		eta = DEFAULT_eta;
		t_min = DEFAULT_t_min;
		t_max = DEFAULT_t_max;
		// Init aux vars
		t_previous = Double.NaN;
		// Init state vars
		t_current = t_min;
		load = Double.NaN;
	}
	
	/*
	 * Events:
	 */
	
	public void doRun() {
		// Observe FEL for simulation stop
		if (getEventList() instanceof MessagingEventList) {
			((MessagingEventList) getEventList()).addPropertyChangeListener(
					MessagingEventList.PROP_STOPPING,
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							doStop();
						}
					});
		}
		// Announce initial state
		firePropertyChange(PROP_TEMPERATURE, t_previous, t_current);
	}
	
	/**
	 * This method should allow the fridge to update its state for statistical
	 * data collection a last time before the simulation stops.
	 */
	public abstract void doStop();
	
	/*
	 * Getters & Setters:
	 */

	/**
	 * @return the t_surround
	 */
	public double getT_surround() {
		return t_surround;
	}

	/**
	 * @param t_surround the t_surround to set
	 */
	public void setT_surround(double t_surround) {
		this.t_surround = t_surround;
	}

	/**
	 * @return the a
	 */
	public double getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(double a) {
		this.a = a;
	}

	/**
	 * @return the q_cooling
	 */
	public double getQ_cooling() {
		return q_cooling;
	}

	/**
	 * @param q_cooling the q_cooling to set
	 */
	public void setQ_cooling(double q_cooling) {
		this.q_cooling = q_cooling;
	}

	/**
	 * @return the q_warming
	 */
	public double getQ_warming() {
		return q_warming;
	}

	/**
	 * @param q_warming the q_warming to set
	 */
	public void setQ_warming(double q_warming) {
		this.q_warming = q_warming;
	}

	/**
	 * @return the m_c
	 */
	public double getM_c() {
		return m_c;
	}

	/**
	 * @param m_c the m_c to set
	 */
	public void setM_c(double m_c) {
		this.m_c = m_c;
	}

	/**
	 * @return the eta
	 */
	public double getEta() {
		return eta;
	}

	/**
	 * @param eta the eta to set
	 */
	public void setEta(double eta) {
		this.eta = eta;
	}

	/**
	 * @return the t_min
	 */
	public double getT_min() {
		return t_min;
	}

	/**
	 * @param t_min the t_min to set
	 */
	public void setT_min(double t_min) {
		this.t_min = t_min;
	}

	/**
	 * @return the t_max
	 */
	public double getT_max() {
		return t_max;
	}

	/**
	 * @param t_max the t_max to set
	 */
	public void setT_max(double t_max) {
		this.t_max = t_max;
	}

	/**
	 * @return the t_current
	 */
	public double getT_current() {
		return t_current;
	}

	/**
	 * @param t_current the t_current to set
	 */
	public void setT_current(double t_current) {
		this.t_current = t_current;
	}

	/**
	 * @return the load
	 */
	public double getLoad() {
		return load;
	}

	/**
	 * @param load the load to set
	 */
	public void setLoad(double load) {
		double bak = this.load;
		this.load = load;
		// Announce state change
		firePropertyChange(IterativeFridge.PROP_LOAD, bak, this.load);
	}
	
	/**
	 * @return whether this device starts in state 'cooling'
	 */
	public boolean isStartActive() {
		return startActive;
	}

	/**
	 * @param startActive
	 *            whether this device starts in state 'cooling'
	 */
	public void setStartActive(boolean startActive) {
		this.startActive = startActive;
	}

	/**
	 * @return whether the cooling device is active
	 */
	public boolean isActive() {
		return load > q_warming;
	}

	/**
	 * @return the controller
	 */
	public AbstractController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(AbstractController controller) {
		this.controller = controller;
	}

	/*
	 * Parameter variance util methods:
	 */

	/**
	 * Performs a distribution of the parameter values using random numbers
	 * produced sequentially by the given variate.
	 */
	public void variateAllSequential(RandomVariate rv) {
		variate_a(rv);
		variate_eta(rv);
		variate_mC(rv);
		variate_qCooling(rv);
		variate_qWarming(rv);
		variate_tSurround(rv);
		variate_tCurrent(rv);
	}
	
	/**
	 * Performs a distribution of the parameter values using the first random
	 * number produced by the given variate for all values.
	 * 
	 * @param rv
	 */
	public void variateAllParallel(RandomVariate rv) {
		double var = rv.generate();
		a *= var;
		eta *= var;
		m_c *= var;
		q_cooling *= var;
		q_warming *= var;
		t_surround *= var;
		t_current *= var;
	}
	
	/**
	 * Performs a distribution of the parameter '<code>a</code>' using a
	 * random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_a(RandomVariate rv) {
		a *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>eta</code>' using a
	 * random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_eta(RandomVariate rv) {
		eta *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>m_c</code>' using a
	 * random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_mC(RandomVariate rv) {
		m_c *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>q_cooling</code>'
	 * using a random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_qCooling(RandomVariate rv) {
		q_cooling *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>q_warming</code>'
	 * using a random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_qWarming(RandomVariate rv) {
		q_warming *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>t_surround</code>'
	 * using a random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_tSurround(RandomVariate rv) {
		t_surround *= rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>t_current</code>'
	 * using a random number produced by the given variate.
	 * 
	 * @param rv
	 */
	public void variate_tCurrent(RandomVariate rv) {
		t_current *= rv.generate();
	}
	
	/**
	 * Performs a distribution by setting the parameters to the values produced
	 * by the given variate.
	 * 
	 * @param rv
	 */
	public void generateAllSequential(RandomVariate rv) {
		generate_a(rv);
		generate_eta(rv);
		generate_mC(rv);
		generate_qCooling(rv);
		generate_qWarming(rv);
		generate_tSurround(rv);
		generate_tCurrent(rv);
	}
	
	/**
	 * Performs a distribution of the parameter '<code>a</code>' by setting
	 * it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_a(RandomVariate rv) {
		a = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>eta</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_eta(RandomVariate rv) {
		eta = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>m_c</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_mC(RandomVariate rv) {
		m_c = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>q_cooling</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_qCooling(RandomVariate rv) {
		q_cooling = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>q_warming</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_qWarming(RandomVariate rv) {
		q_warming = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>t_surround</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_tSurround(RandomVariate rv) {
		t_surround = rv.generate();
	}
	
	/**
	 * Performs a distribution of the parameter '<code>t_current</code>' by
	 * setting it to the value produced next by the given variate.
	 * 
	 * @param rv
	 */
	public void generate_tCurrent(RandomVariate rv) {
		t_current = rv.generate();
	}
	
	/*
	 * Misc:
	 */
	
	/**
	 * Updates the internal field t_current, fires a property change event and
	 * returns the new value.
	 */
	public abstract double updateTemperature();
	
	/**
	 * Calculate time needed to reach temperature <code>t_dest</code>,
	 * starting at temperature <code>t_from</code>. Calculation is based on
	 * given load und uses additional internal parameters of this fridge like
	 * t_surround, eta, a and m_c.
	 * 
	 * @param t_from
	 * @param t_dest
	 * @return
	 */
	public double tau(double t_from, double t_dest, double load) {
		double dividend = t_dest - t_surround + (eta * (load / a));
		double divisor = t_from - t_surround + (eta * (load / a));
		double tau = -1 * Math.log((dividend / divisor)) * (m_c / a);
		// Multiply by 60 because tau is calculated in hours, but simulation
		// uses minutes
		return tau * 60.0;
	}
	
	/**
	 * Calculate time needed to reach temperature <code>t_dest</code>,
	 * starting at temperature <code>t_from</code>. Calculation is based on
	 * linear approximation by using precalculated tau_cooling or tau_warming.
	 * <p>
	 * Warning: tau_cooling and tau_warming are based on q_cooling and q_warming
	 * respectively. So this method depends on these values, too. If another
	 * load should be assumed, use method
	 * <code>tau(double t_from, double t_dest, double load)</code>, which is
	 * a little bit slower in calculation, but more precise and flexible.
	 * 
	 * @param t_from
	 * @param t_dest
	 * @return
	 */
	public double tau(double t_from, double t_dest) {
		// Calculate range from t_min to t_max
		double range = getT_max() - getT_min();
		// Check direction: warming or cooling
		if (t_from < t_dest) {
			// Calculate tau_warming if not already done
			if (Double.isNaN(tau_warming)) {
				tau_warming = tau(getT_min(), getT_max(), getQ_warming());
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_warming
			return ((t_dest - t_from) / range) * tau_warming;
		} else {
			// Calculate tau_cooling if not already done
			if (Double.isNaN(tau_cooling)) {
				tau_cooling = tau(getT_max(), getT_min(), getQ_cooling());
			}
			// Calculate fraction of desired range by maximal range and return
			// resulting proportion of tau_cooling
			return -((t_dest - t_from) / range) * tau_cooling;
		}
	}
}