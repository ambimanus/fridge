package de.uniol.ui.desync;

import simkit.random.LKSeeds;

public class Configuration {

	/* Constants */
	/** Available model types */
	public static enum MODELS {
		ITERATIVE, LINEAR, COMPACT_LINEAR
	}
	/** Available strategies*/
	public static enum STRATEGIES {
		NONE, DIRECT, TIMED
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
	public int POPULATION_SIZE = 1000;
	/** Length of simulation, 1 unit == 1 hour */
	public double SIMULATION_LENGTH = 10.0;
	/** Used model type */
	public MODELS model = MODELS.COMPACT_LINEAR;
	/** Used strategy */
	public STRATEGIES strategy = STRATEGIES.TIMED;
	/** Show results? */
	public boolean showResults = true;
	
	/* Strategy params: direct storage control */
	public double direct_t_notify = 90.0;
	public double direct_spread = 10.0;
	public boolean direct_doUnload = false;
	
	/* Strategy params: timed load reduction */
	public double timed_t_notify = 90.0;
	public double timed_tau_activ = 29.0;
	public double timed_tau_reduce = 100.0;
}