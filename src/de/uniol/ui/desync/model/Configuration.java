package de.uniol.ui.desync.model;

import simkit.random.LKSeeds;

public class Configuration {
	
	/** instance-counter */
	public static int instance = -1;
	/** distinction-counter */
	public static int distinct = -1;
	
	public Configuration() {
		instance++;
		title = "New Configuration"
				+ (Configuration.instance == 0 ? ""
						: (" " + Configuration.instance));
	}

	/* Constants */
	/** Available model types */
	public static enum VARIATE {
		NONE, UNIFORM, NORMAL
	}
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
	
	/* Global params */
	/** Title of this configuration */
	public String title = "New Configuration";

	/* Population params */
	/** The propability a fridge is set active at simulation start */
	public double ACTIVE_AT_START_PROPABILITY = 0.22;
	
	/** Variation of T_current */
	public VARIATE variate_Tcurrent = VARIATE.UNIFORM;
	/** Seed for Tcurrent variation generator */
	public long variate_Tcurrent_seed = LKSeeds.ZRNG[1];
	/** Tcurrent default */
	public double variate_Tcurrent_default = 5.5;
	/** Tcurrent minimum */
	public double variate_Tcurrent_min = 3.0;
	/** Tcurrent maximum */
	public double variate_Tcurrent_max = 8.0;
	/** standard deviation for Tcurrent normal distribution */
	public double variate_Tcurrent_sdev = 0.75;
	
	/** Variation of m_c */
	public VARIATE variate_mc = VARIATE.NONE;
	/** Seed for mc variation generator */
	public long variate_mc_seed = LKSeeds.ZRNG[2];
	/** mc default */
	public double variate_mc_default = 19.95;
	/** Thermal mass minimum */
	public double variate_mc_min = 7.9;
	/** Thermal mass maximum */
	public double variate_mc_max = 32.0;
	/** standard deviation for mc normal distribution */
	public double variate_mc_sdev = 3.5;
	
	/** Variation of A */
	public VARIATE variate_A = VARIATE.NONE;
	/** Seed for A variation generator */
	public long variate_A_seed = LKSeeds.ZRNG[3];
	/** A default */
	public double variate_A_default = 3.21;
	/** A minimum */
	public double variate_A_min = 2.889;
	/** A maximum */
	public double variate_A_max = 3.531;
	/** standard deviation for A normal distribution */
	public double variate_A_sdev = 0.09;
	
	/** Variation of TO */
	public VARIATE variate_TO = VARIATE.NONE;
	/** Seed for TO variation generator */
	public long variate_TO_seed = LKSeeds.ZRNG[4];
	/** TO default */
	public double variate_TO_default = 20.0;
	/** TO minimum */
	public double variate_TO_min = 15.0;
	/** TO maximum */
	public double variate_TO_max = 25.0;
	/** standard deviation for TO normal distribution */
	public double variate_TO_sdev = 1.5;
	
	/** Variation of eta */
	public VARIATE variate_eta = VARIATE.NONE;
	/** Seed for eta variation generator */
	public long variate_eta_seed = LKSeeds.ZRNG[5];
	/** eta default */
	public double variate_eta_default = 3.0;
	/** eta minimum */
	public double variate_eta_min = 2.0;
	/** eta maximum */
	public double variate_eta_max = 4.0;
	/** standard deviation for eta normal distribution */
	public double variate_eta_sdev = 0.25;
	
	/** Variation of qc */
	public VARIATE variate_qc = VARIATE.NONE;
	/** Seed for qc variation generator */
	public long variate_qc_seed = LKSeeds.ZRNG[6];
	/** qc default */
	public double variate_qc_default = 0.0;
	/** qc minimum */
	public double variate_qc_min = 0.0;
	/** qc maximum */
	public double variate_qc_max = 5.0;
	/** standard deviation for qc normal distribution */
	public double variate_qc_sdev = 0.75;
	
	/** Variation of qw */
	public VARIATE variate_qw = VARIATE.NONE;
	/** Seed for qw variation generator */
	public long variate_qw_seed = LKSeeds.ZRNG[7];
	/** qw default */
	public double variate_qw_default = 70.0;
	/** qw minimum */
	public double variate_qw_min = 50.0;
	/** qw maximum */
	public double variate_qw_max = 90.0;
	/** standard deviation for qw normal distribution */
	public double variate_qw_sdev = 6.0;
	
	/* Simulation params */
	/** Simulation repititions with different seeds */
	public int repetitions = 5;
	/** Amount of simulated fridges */
	public int POPULATION_SIZE = 5000;
	/** Length of simulation, 1 unit == 1 hour */
	public double SIMULATION_LENGTH = 10.0;
	/** Used model type */
	public MODEL model = MODEL.COMPACT_LINEAR;
	/** Used strategy */
	public SIGNAL strategy = SIGNAL.NONE;
	/** Used damping */
	public DAMPING damping = DAMPING.NONE;
	
	/** Show progress? */
	public boolean showProgress = true;
	/** Show results? */
	public boolean showResults = false;
	/** Include lamps? */
	public boolean includeLamps = false;
	
	/* Strategy params: direct storage control */
	public double direct_t_notify = 90.0;
	public double direct_spread = 10.0;
	public boolean direct_doUnload = false;
	
	/* Strategy params: timed load reduction */
	public double timed_t_notify = 90.0;
	public double timed_tau_preload = 30.0;
	public double timed_tau_reduce = 120.0;
}