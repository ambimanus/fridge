package de.uniol.ui.desync;

import simkit.random.LKSeeds;

public class Configuration {
	
	/** instance-counter */
	private static int instance = -1;
	
	public Configuration() {
		instance++;
	}

	/* Constants */
	/** Available model types */
	public static enum MODEL {
		ITERATIVE, LINEAR, COMPACT_LINEAR
	}
	/** Available strategies */
	public static enum SIGNAL {
		NONE, DSC, TLR
	}
	/** Available damping strategies */
	public static enum DAMPING {
		NONE, RANDOM, STATEFUL_HALF, STATEFUL_FULL
	}

	/* Population params */
	/** Thermal mass minimum */
	public double MC_MIN = 7.9;
	/** Thermal mass maximum */
	public double MC_MAX = 32.0;
	/** Perform variation of m_c ? */
	public boolean variate_mc = false;
	/** Seed for mc variation generator */
	public long variate_mc_seed = LKSeeds.ZRNG[1];
	/** Perform variation of T_current ? */
	public boolean variate_Tcurrent = true;
	/** Seed for Tcurrent variation generator */
	public long variate_Tcurrent_seed = LKSeeds.ZRNG[99];
	/** The propability a fridge is set active at simulation start */
	public double ACTIVE_AT_START_PROPABILITY = 0.22;
	
	/* Simulation params */
	/** Amount of simulated fridges */
	public int POPULATION_SIZE = 5000;
	/** Length of simulation, 1 unit == 1 hour */
	public double SIMULATION_LENGTH = 10.0;
	/** Used model type */
	public MODEL model = MODEL.COMPACT_LINEAR;
	/** Used strategy */
	public SIGNAL strategy = SIGNAL.TLR;
	/** Used damping */
	public DAMPING damping = DAMPING.RANDOM;
	
	/** Show progress? */
	public boolean showProgress = true;
	/** Show results? */
	public boolean showResults = true;
	
	/* Strategy params: direct storage control */
	public double direct_t_notify = 90.0;
	public double direct_spread = 10.0;
	public boolean direct_doUnload = false;
	
	/* Strategy params: timed load reduction */
	public double timed_t_notify = 90.0;
	public double timed_tau_activ = 30.0;
	public double timed_tau_reduce = 120.0;
}