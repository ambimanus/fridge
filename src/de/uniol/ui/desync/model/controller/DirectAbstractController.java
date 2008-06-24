package de.uniol.ui.desync.model.controller;

import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

public abstract class DirectAbstractController extends AbstractController {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";

	protected AbstractFridge fridge;
	protected static RandomVariate random;
	
	public DirectAbstractController(AbstractFridge fridge) {
		this.fridge = fridge;
		setEventListID(fridge.getEventListID());
		if (random == null) {
			random = new UniformVariate();
		}
	}
	
	protected double drawUniformRandom(double low, double high) {
		Object[] params = random.getParameters();
		if (params.length != 2) {
			throw new IllegalArgumentException(
					"Expected params [low,high], but got " + params);
		}
		if ((Double)params[0] != low || (Double)params[1] != high) {
			random.setParameters(low, high);
		}
		return random.generate();
	}
	
	public abstract void doLoadThermalStorage(Double t_preload, Double spread);
	
	public abstract void doUnloadThermalStorage(Double t_preload, Double spread);
}