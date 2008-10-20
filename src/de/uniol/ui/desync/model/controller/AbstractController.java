package de.uniol.ui.desync.model.controller;

import simkit.random.BernoulliVariate;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

/**
 * Abstract base class of a controller as SimEntity. Contains random number generators.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public abstract class AbstractController extends SimEntityClean {

	protected AbstractFridge fridge;
	private static RandomVariate uniform;
	private static RandomVariate bernoulli;

	private double lastLow = Double.NaN;
	private double lastHigh = Double.NaN;
	private double lastPropability = Double.NaN;

	public AbstractController(AbstractFridge fridge, int eventListID) {
		super(eventListID);
		this.fridge = fridge;
		setName(getClass().getSimpleName() + " for " + fridge.getName());
		setEventListID(fridge.getEventListID());
		if (uniform == null) {
			uniform = new UniformVariate();
		}
		if (bernoulli == null) {
			bernoulli = new BernoulliVariate();
		}
	}

	protected double drawUniformRandom(double low, double high) {
		if (lastLow != low || lastHigh != high) {
			uniform.setParameters(low, high);
			lastLow = low;
			lastHigh = high;
		}
		return uniform.generate();
	}
	
	protected double drawBernoulli(double propability) {
		if (lastPropability != propability) {
			bernoulli.setParameters(propability);
			lastPropability = propability;
		}
		return bernoulli.generate();
	}

	/**
	 * @return the fridge
	 */
	public AbstractFridge getFridge() {
		return fridge;
	}

	public abstract void doRun();
}