package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;

/**
 * Iterative implementation of a fridge controller. This model calculates
 * temperature and load values on each simulated time step.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */

public class BaseControllerIterative extends AbstractController {

	/* event constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	public final static String EV_WARMING = "Warming";
	public final static String EV_COOLING = "Cooling";
	
	public BaseControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doRun() {
		// Check if we were set active externally
		if (fridge.isStartActive()) {
			// Target to min temp
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
		} else {
			// Be passive until necessary:
			if (fridge.getT_current() < fridge.getT_max()) {
				// Cool enough, start in warming phase immediately
				waitDelay(EV_BEGIN_WARMING, 0.0, fridge.getQ_warming());
			} else {
				// Too warm, start cooling immediately
				waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
			}
		}
//		// The following snippet tests the long run temperature calculation.
//		State s = new State(fridge.getT_min(), fridge.getQ_warming(), false);
//		NumberFormat nf = NumberFormat.getInstance();
//		nf.setMinimumFractionDigits(0);
//		nf.setMaximumFractionDigits(0);
//		nf.setMinimumIntegerDigits(3);
//		nf.setMaximumIntegerDigits(3);
//		for (double i = 0.0; i < 600.0; i += 10.0) {
//			System.out.println(nf.format(i) + " - "
//					+ fridge.calculateStateAfterLongRun(s, i));
//		}
	}
	
	public void doBeginCooling(Double load) {
		// Update load
		fridge.setLoad(load);
		// Delay next state change
		waitDelay(EV_COOLING, ((IterativeFridge)fridge).getTau());
	}
	
	public void doBeginWarming(Double load) {
		// Update load
		fridge.setLoad(load);
		// Delay next state change
		waitDelay(EV_WARMING, ((IterativeFridge)fridge).getTau());
	}

	public void doCooling() {
		// Calculate and announce new temperature
		fridge.updateTemperature();
		// Delay next state change
		if(fridge.getT_current() <= fridge.getT_min()) {
			// Cool enough, switch to 'warming' immediately
			waitDelay(EV_BEGIN_WARMING, 0.0, fridge.getQ_warming());
		} else {
			// Desired temperature not reached, continue cooling
			waitDelay(EV_COOLING, ((IterativeFridge)fridge).getTau());
		}
	}

	public void doWarming() {
		// Calculate and announce new temperature
		fridge.updateTemperature();
		// Delay next state change
		if(fridge.getT_current() >= fridge.getT_max()) {
			// Too warm, start cooling immediately
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
		} else {
			// Still cool enough, continue warming
			waitDelay(EV_WARMING, ((IterativeFridge)fridge).getTau());
		}
	}
}