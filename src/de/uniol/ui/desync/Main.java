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
import de.uniol.ui.desync.model.controller.ControllerCompactLinear;
import de.uniol.ui.desync.model.controller.ControllerIterative;
import de.uniol.ui.desync.model.controller.ControllerLinear;
import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.fridges.CompactLinearFridge;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.fridges.LinearFridge;
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
	public static enum MODES {
		ITERATIVE, LINEAR, COMPACT_LINEAR
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
	public final static int POPULATION_SIZE = 5000;
	/** Length of simulation, 1 unit == 1 hour */
	public final static double SIMULATION_LENGTH = 20.0;
	/** Used model type */
	public final static MODES mode = MODES.COMPACT_LINEAR;
	
	/* Direct storage control params */
	public final static double t_notify = 1; // hours
	public final static double t_preload = 1; // hours
	public final static double spread = 60.0; // minutes
	public final static boolean doUnload = false;

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

		// Create fridges
		ArrayList<AbstractFridge> fridges = createFridges(list);
		
		// Create controllers
		ArrayList<AbstractController> controllers = createControllers(fridges);
		ControlCenter cc = new ControlCenter(controllers, t_notify * 60.0,
				t_preload * 60.0, spread, doUnload);
		cc.setEventListID(list);
		
		// Prepare simulation
		Simulation sim = new Simulation(el, fridges);
		sim.setCollectTemperature(mode == MODES.ITERATIVE);

		// Simulate
		sim.simulate(SIMULATION_LENGTH * 60.0);

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
		Congruential c = new Congruential();
		c.setSeed(CongruentialSeeds.SEED[3]);
		thermalMassVariate.setRandomNumber(c);
		thermalMassVariate.setParameters(MC_MIN, MC_MAX);
		RandomVariate tCurrentVariate = new UniformVariate();
		c = new Congruential();
		c.setSeed(CongruentialSeeds.SEED[8]);
		tCurrentVariate.setRandomNumber(c);
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
			switch (mode) {
			case ITERATIVE: {
				f = new IterativeFridge();
				break;
			}
			case LINEAR: {
				f = new LinearFridge();
				break;
			}
			case COMPACT_LINEAR: {
				f = new CompactLinearFridge();
				break;
			}
			}
			// Equally distribute m_c between MC_MIN and MC_MAX
			f.generate_mC(thermalMassVariate);
			// Equally distribute t_current between t_min and t_max
			f.generate_tCurrent(tCurrentVariate);
			// Make (ACTIVE_AT_START_PROPABILITY*100)% of the fridges active
			f.setStartActive(activityAtStartVariate.generate() > 0);
			f.setEventListID(list);
			fridges.add(f);
		}
		return fridges;
	}
	
	private static ArrayList<AbstractController> createControllers(
			ArrayList<AbstractFridge> fridges) {
		ArrayList<AbstractController> controllers = new ArrayList<AbstractController>();
		for (AbstractFridge af : fridges) {
			AbstractController c = null;
			switch (mode) {
			case ITERATIVE: {
				c = new ControllerIterative((IterativeFridge) af);
				break;
			}
			case LINEAR: {
				c = new ControllerLinear((LinearFridge) af);
				break;
			}
			case COMPACT_LINEAR: {
				c = new ControllerCompactLinear((CompactLinearFridge) af);
				break;
			}
			}
			controllers.add(c);
		}
		return controllers;
	}
}