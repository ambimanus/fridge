package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;

public class BaseControllerIterative extends AbstractController {

	/* event constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	public final static String EV_WARMING = "Warming";
	public final static String EV_COOLING = "Cooling";
	
	public BaseControllerIterative(IterativeFridge fridge) {
		super(fridge);
	}
	
	public void doRun() {
		// Check if we were set active externally
		if (fridge.isStartActive()) {
			// Target to min temp
			waitDelay(EV_BEGIN_COOLING, 0.0);
		} else {
			// Be passive until necessary:
			if (fridge.getT_current() < fridge.getT_max()) {
				// Cool enough, start in warming phase immediately
				waitDelay(EV_BEGIN_WARMING, 0.0);
			} else {
				// Too warm, start cooling immediately
				waitDelay(EV_BEGIN_COOLING, 0.0);
			}
		}
	}
	
	public void doBeginCooling() {
		// Update load
		fridge.setLoad(fridge.getQ_cooling());
		// Delay next state change
		waitDelay(EV_COOLING, IterativeFridge.SIMULATION_CLOCK);
	}
	
	public void doBeginWarming() {
		// Update load
		fridge.setLoad(fridge.getQ_warming());
		// Delay next state change
		waitDelay(EV_WARMING, IterativeFridge.SIMULATION_CLOCK);
	}

	public void doCooling() {
		// Calculate and announce new temperature
		fridge.updateTemperature();
		// Delay next state change
		if(fridge.getT_current() <= fridge.getT_min()) {
			// Cool enough, switch to 'warming' immediately
			waitDelay(EV_BEGIN_WARMING, 0.0);
		} else {
			// Desired temperature not reached, continue cooling
			waitDelay(EV_COOLING, IterativeFridge.SIMULATION_CLOCK);
		}
	}

	public void doWarming() {
		// Calculate and announce new temperature
		fridge.updateTemperature();
		// Delay next state change
		if(fridge.getT_current() >= fridge.getT_max()) {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0);
		} else {
			// Still cool enough, continue warming
			waitDelay(EV_WARMING, IterativeFridge.SIMULATION_CLOCK);
		}
	}
}