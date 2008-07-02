package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.ArrayList;

import simkit.Schedule;
import simkit.random.BernoulliVariate;
import simkit.random.Congruential;
import simkit.random.CongruentialSeeds;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.ControlCenter;
import de.uniol.ui.desync.model.Simulation;
import de.uniol.ui.desync.model.controller.AbstractController;
import de.uniol.ui.desync.model.controller.BaseControllerCompactLinear;
import de.uniol.ui.desync.model.controller.BaseControllerIterative;
import de.uniol.ui.desync.model.controller.BaseControllerLinear;
import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.controller.DirectControllerIterative;
import de.uniol.ui.desync.model.controller.DirectControllerLinear;
import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.controller.TimedControllerIterative;
import de.uniol.ui.desync.model.controller.TimedControllerLinear;
import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.strategies.AbstractStrategyPerformer;
import de.uniol.ui.desync.model.strategies.StrategyPerformerDirect;
import de.uniol.ui.desync.model.strategies.StrategyPerformerTimed;
import de.uniol.ui.desync.util.MessagingEventList;

/**
 * This class is the main class of this simulation. It creates all entities and
 * prepares and runs the simulation.
 * 
 * @author Chh
 */
public class Main {

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
	public final static double MC_MIN = 7.9;
	/** Thermal mass maximum */
	public final static double MC_MAX = 32.0;
	/** The propability a fridge is set active at simulation start */
	public final static double ACTIVE_AT_START_PROPABILITY = 0.22;

	/* Simulation params */
	/** Amount of simulated fridges */
	public final static int POPULATION_SIZE = 1;
	/** Length of simulation, 1 unit == 1 hour */
	public final static double SIMULATION_LENGTH = 10.0;
	/** Used model type */
	public final static MODELS model = MODELS.COMPACT_LINEAR;
	/** Used strategy */
	public final static STRATEGIES strategy = STRATEGIES.NONE;
	
	/* Strategy params: direct storage control */
	public final static double direct_t_notify = 60.0;
	public final static double direct_spread = 30.0;
	public final static boolean direct_doUnload = false;
	
	/* Strategy params: timed load reduction */
	public final static double timed_t_notify = 60.0;
	public final static double timed_tau_activ = 20.0;
	public final static double timed_tau_reduce = 30.0;

	/**
	 * Creates all entities and prepares and runs the experiment.
	 * 
	 * @param args
	 *            not used yet
	 */
	public static void main(String[] args) {
		// Prepare FEL
		final int list = Schedule.addNewEventList(MessagingEventList.class);
		final MessagingEventList el = (MessagingEventList) Schedule
				.getEventList(list);
		el.setVerbose(true);

		// Create fridges
		ArrayList<AbstractFridge> fridges = createFridges(list);
		if (strategy != STRATEGIES.NONE) {
			// Create ControlCenter and Strategy
			new ControlCenter(list, fridges, createStrategy(list));
		}
		
		// Prepare simulation
		Simulation sim = new Simulation(el, fridges);
		sim.setCollectTemperature(model == MODELS.ITERATIVE
				|| POPULATION_SIZE == 1);

		// Simulate
		double start = System.currentTimeMillis();
		sim.simulate(SIMULATION_LENGTH * 60.0);
		double end = System.currentTimeMillis() - start;
System.out.println("Elapsed time: " + end);

		// Create charts
		sim.showResults(POPULATION_SIZE < 10, POPULATION_SIZE > 1
				&& POPULATION_SIZE < 10, SystemColor.BLACK);
	}

	/**
	 * Creates the fridges population using the runtime type defined in
	 * {@link mode}.
	 * 
	 * @param list
	 *            the underlying FEL id
	 * @return the population
	 */
	private static ArrayList<AbstractFridge> createFridges(int list) {
		// Create distinct uniform random variates for m_c and t_current
		RandomVariate thermalMassVariate = new UniformVariate();
		Congruential cong = new Congruential();
		cong.setSeed(CongruentialSeeds.SEED[3]);
		thermalMassVariate.setRandomNumber(cong);
		thermalMassVariate.setParameters(MC_MIN, MC_MAX);
		RandomVariate tCurrentVariate = new UniformVariate();
		cong = new Congruential();
		cong.setSeed(CongruentialSeeds.SEED[8]);
		tCurrentVariate.setRandomNumber(cong);
		tCurrentVariate.setParameters(AbstractFridge.DEFAULT_t_min,
				AbstractFridge.DEFAULT_t_max);
		// Create [0|1] variate for starting states
		RandomVariate activityAtStartVariate = new BernoulliVariate();
		activityAtStartVariate.setParameters(ACTIVE_AT_START_PROPABILITY);
		// Create fridges
		ArrayList<AbstractFridge> fridges = new ArrayList<AbstractFridge>(
				POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			AbstractFridge f = null;
			AbstractController c = null;
			switch (model) {
			case ITERATIVE: {
				f = new IterativeFridge();
				switch (strategy) {
				case NONE: {
					c = new BaseControllerIterative((IterativeFridge) f);
					break;
				}
				case DIRECT: {
					c = new DirectControllerIterative((IterativeFridge) f);
					break;
				}
				case TIMED: {
					c = new TimedControllerIterative((IterativeFridge) f);
					break;
				}
				}
				break;
			}
			case LINEAR: {
				f = new LinearFridge();
				switch (strategy) {
				case NONE: {
					c = new BaseControllerLinear((LinearFridge) f);
					break;
				}
				case DIRECT: {
					c = new DirectControllerLinear((LinearFridge) f);
					break;
				}
				case TIMED: {
					c = new TimedControllerLinear((LinearFridge) f);
					break;
				}
				}
				break;
			}
			case COMPACT_LINEAR: {
				f = new LinearFridge();
				switch (strategy) {
				case NONE: {
					c = new BaseControllerCompactLinear((LinearFridge) f);
					break;
				}
				case DIRECT: {
					c = new DirectControllerCompactLinear((LinearFridge) f);
					break;
				}
				case TIMED: {
					c = new TimedControllerCompactLinear((LinearFridge) f);
					break;
				}
				}
				break;
			}
			}
			// Equally distribute m_c between MC_MIN and MC_MAX
//			f.generate_mC(thermalMassVariate);
			// Equally distribute t_current between t_min and t_max
//			f.generate_tCurrent(tCurrentVariate);
			// Make (ACTIVE_AT_START_PROPABILITY*100)% of the fridges active
//			f.setStartActive(activityAtStartVariate.generate() > 0);
			// Assign proper FEL
			f.setEventListID(list);
			c.setEventListID(list);
			// Assign controller
			f.setController(c);
			// Store created fridge
			fridges.add(f);
		}
		return fridges;
	}
	
	private static AbstractStrategyPerformer createStrategy(int eventListID) {
		switch (strategy) {
		case NONE: {
			return null;
		}
		case DIRECT: {
			return new StrategyPerformerDirect(eventListID, direct_t_notify,
					direct_spread, direct_doUnload);
		}
		case TIMED: {
			return new StrategyPerformerTimed(eventListID, timed_t_notify,
					timed_tau_activ, timed_tau_reduce);
		}
		}
		return null;
	}
}