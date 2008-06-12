package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.ArrayList;

import simkit.Schedule;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.model.Experiment;
import de.uniol.ui.model.Fridge;

public class Main {

	/* Population params */
	/** Thermal mass minimum */
	public final static double MC_MIN = 7.9;
	/** Thermal mass maximum */
	public final static double MC_MAX = 32.0;

	/* Simulation params */
	public final static int POPULATION_SIZE = 1000;
	public final static double SIMULATION_LENGTH = 1800.0;

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
		exp.showResults(false, false, SystemColor.BLACK);
	}

	/**
	 * Creates the fridges population.
	 * 
	 * @param list the underlying FEL id
	 * @return the population
	 */
	private static ArrayList<Fridge> createFridges(int list) {
		RandomVariate thermalMassVariate = new UniformVariate();
		thermalMassVariate.setParameters(MC_MIN, MC_MAX);
		ArrayList<Fridge> fridges = new ArrayList<Fridge>(POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Fridge f = new Fridge();
			f.generate_mC(thermalMassVariate);
			f.setEventListID(list);
			fridges.add(f);
		}
		return fridges;
	}
}