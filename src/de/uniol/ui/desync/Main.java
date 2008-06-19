package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.ArrayList;

import simkit.Schedule;
import simkit.random.BernoulliVariate;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.model.Experiment;
import de.uniol.ui.model.fridges.AbstractFridge;
import de.uniol.ui.model.fridges.CompactLinearFridge;
import de.uniol.ui.model.fridges.IterativeFridge;
import de.uniol.ui.model.fridges.LinearFridge;

/**
 * This class is the main class of this simulation. It creates all entities and
 * prepares and runs the experiment.
 * 
 * @author Chh
 */
public class Main {

	/* Constants */
	/** Available model types */
	public static enum MODES {
		ITERATIV, LINEAR, COMPACT_LINEAR
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
	/** Lenght of simulation, 1 unit == 1 minute */
	public final static double SIMULATION_LENGTH = 1800.0;
	/** Used model type */
	public final static MODES mode = MODES.COMPACT_LINEAR;

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

		// Prepare experiment
		Experiment exp = new Experiment(el, createFridges(list));

		// Simulate
		exp.simulate(SIMULATION_LENGTH);

		// Create charts
		exp.showResults(POPULATION_SIZE < 10, POPULATION_SIZE > 1
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
		RandomVariate thermalMassVariate = new UniformVariate();
		thermalMassVariate.setParameters(MC_MIN, MC_MAX);
		RandomVariate tCurrentVariate = new UniformVariate();
		RandomVariate activityAtStartVariate = new BernoulliVariate();
		activityAtStartVariate.setParameters(ACTIVE_AT_START_PROPABILITY);
		tCurrentVariate.setParameters(AbstractFridge.DEFAULT_t_min,
				AbstractFridge.DEFAULT_t_max);
		ArrayList<AbstractFridge> fridges = new ArrayList<AbstractFridge>(
				POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			AbstractFridge f = null;
			switch (mode) {
			case ITERATIV: {
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
			if (activityAtStartVariate.generate() > 0) {
				f.setLoad(AbstractFridge.DEFAULT_q_cooling);
			} else {
				f.setLoad(AbstractFridge.DEFAULT_q_warming);
			}
			f.setEventListID(list);
			fridges.add(f);
		}
		return fridges;
	}
}