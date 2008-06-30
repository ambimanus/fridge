package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;

public class BaseControllerLinear extends AbstractController {

	/* event constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	
	public BaseControllerLinear(LinearFridge fridge) {
		super(fridge);
	}
	
	public void doRun() {
		// Check if we were set active externally
		if (fridge.isStartActive()) {
			// Target to min temp
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
		} else {
			// Load is undefined, start in passive mode:
			if (fridge.getT_current() < fridge.getT_max()) {
				// Cool enough, start in warming phase immediately
				waitDelay(EV_BEGIN_WARMING, 0.0, fridge.getT_max(), fridge
						.getQ_warming());
			} else {
				// Too warm, start cooling immediately
				waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getT_min(), fridge
						.getQ_cooling());
			}
		}
	}
	
	public void doBeginCooling(Double t_dest, Double load) {
		// Calculate current temperature based on current load and elapsed time
		double t_current = fridge.updateTemperature();
		// Update load
		fridge.setLoad(load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = fridge.tau(t_current, t_dest);
		// Remove any next scheduled warming event if present
		interrupt(EV_BEGIN_WARMING);
		// Delay next warming phase when cooling is finished
		waitDelay(EV_BEGIN_WARMING, timespan, fridge.getT_max(), fridge
				.getQ_warming());
	}

	public void doBeginWarming(Double t_dest, Double load) {
		// Calculate current temperature based on current load and elapsed time
		double t_current = fridge.updateTemperature();
		// Update load
		fridge.setLoad(load);
		// Calcuate time to stay in cooling state to reach t_dest
		double timespan = fridge.tau(t_current, t_dest);
		// Remove any next scheduled cooling event if present
		interrupt(EV_BEGIN_COOLING);
		// Delay next cooling phase when warming is finished
		waitDelay(EV_BEGIN_COOLING, timespan, fridge.getT_min(), fridge
				.getQ_cooling());
	}
}