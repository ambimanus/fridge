package de.uniol.ui.desync.model.controller;

import simkit.SimEntityBase;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

public abstract class AbstractController extends SimEntityBase {

	public final static String EV_LOAD_THERMAL_STORAGE = "LoadThermalStorage";
	public final static String EV_UNLOAD_THERMAL_STORAGE = "UnloadThermalStorage";

	protected AbstractFridge fridge;
	
	public AbstractController(AbstractFridge fridge) {
		this.fridge = fridge;
		setEventListID(fridge.getEventListID());
	}
	
	public abstract void doLoadThermalStorage(Double t_preload);
	
	public abstract void doUnloadThermalStorage(Double t_preload);
}