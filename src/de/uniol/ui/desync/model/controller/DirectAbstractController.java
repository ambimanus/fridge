package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.AbstractFridge;

public abstract class DirectAbstractController extends AbstractController {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";
	
	public DirectAbstractController(AbstractFridge fridge) {
		super(fridge);
	}
	
	public abstract void doLoadThermalStorage(Double tau_preload, Double spread);
	
	public abstract void doUnloadThermalStorage(Double tau_preload, Double spread);
}