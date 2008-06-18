package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.ArrayList;

import simkit.Schedule;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.model.Experiment;
import de.uniol.ui.model.fridges.AbstractFridge;
import de.uniol.ui.model.fridges.CompactLinearFridge;
import de.uniol.ui.model.fridges.Fridge;
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

	/* Simulation params */
	/** Amount of simulated fridges */
	public final static int POPULATION_SIZE = 1000;
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
		ArrayList<AbstractFridge> fridges = new ArrayList<AbstractFridge>(
				POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			AbstractFridge f = null;
			switch (mode) {
			case ITERATIV: {
				f = new Fridge();
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
			f.generate_mC(thermalMassVariate);
			f.setEventListID(list);
			fridges.add(f);
		}
		return fridges;
	}
}