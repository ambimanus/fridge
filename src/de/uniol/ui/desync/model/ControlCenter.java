package de.uniol.ui.desync.model;

import java.util.ArrayList;

import simkit.SimEntityBase;
import de.uniol.ui.desync.model.controller.AbstractController;
import de.uniol.ui.desync.model.strategies.AbstractStrategy;

public class ControlCenter extends SimEntityBase {

	protected ArrayList<AbstractController> controllers;
	protected AbstractStrategy strategy;

	public ControlCenter(int eventListID,
			ArrayList<AbstractController> controllers, AbstractStrategy strategy) {
		setEventListID(eventListID);
		this.controllers = controllers;
		this.strategy = strategy;
	}

	public void doRun() {
		for (AbstractController c : controllers) {
			strategy.waitDelay(AbstractStrategy.EV_APPLY_TO_CONTROLLER, 0.0, c);
		}
	}
}