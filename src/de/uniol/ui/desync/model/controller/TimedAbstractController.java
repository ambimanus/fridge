package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.AbstractFridge;

public abstract class TimedAbstractController extends AbstractController {

	public final static String EV_REDUCE_LOAD = "ReduceLoad";

	public TimedAbstractController(AbstractFridge fridge) {
		super(fridge);
	}

	public abstract void doReduceLoad(Double tau_preload, Double tau_reduce);
}