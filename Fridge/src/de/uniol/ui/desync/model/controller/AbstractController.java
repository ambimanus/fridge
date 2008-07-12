package de.uniol.ui.desync.model.controller;

import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

public abstract class AbstractController extends SimEntityClean {

	protected AbstractFridge fridge;
	private static RandomVariate random;

	private double lastLow = Double.NaN;
	private double lastHigh = Double.NaN;

	public AbstractController(AbstractFridge fridge, int eventListID) {
		super(eventListID);
		this.fridge = fridge;
		setName(getClass().getSimpleName() + " for " + fridge.getName());
		setEventListID(fridge.getEventListID());
		if (random == null) {
			random = new UniformVariate();
		}
	}

	protected double drawUniformRandom(double low, double high) {
		if (lastLow != low || lastHigh != high) {
			random.setParameters(low, high);
			lastLow = low;
			lastHigh = high;
		}
		return random.generate();
	}

	/**
	 * @return the fridge
	 */
	public AbstractFridge getFridge() {
		return fridge;
	}

	public abstract void doRun();
}