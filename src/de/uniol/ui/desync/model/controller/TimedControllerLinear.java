package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Itlr;

public class TimedControllerLinear extends BaseControllerLinear implements
		Itlr {

	public TimedControllerLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		// FIXME implement
	}
}