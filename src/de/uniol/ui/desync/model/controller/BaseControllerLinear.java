package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.LinearFridge;

/**
 * Linear implementation of a fridge controller. This model calculates
 * temperature and load values only on phase changes: First a desired
 * temperature is defined. The model then calculates the time needed to reach
 * this temperature. An event is then scheduled at the calculated point in time.
 * When this event is processed, it schedules another event to reach a newly
 * defined target temperature. Using this paradigm, the model easily switches
 * warming/cooling phases with very little calculations needed.
 * 
 * @author Chh
 */
public class BaseControllerLinear extends AbstractController {

	/* event constants */
	public final static String EV_BEGIN_WARMING = "BeginWarming";
	public final static String EV_BEGIN_COOLING = "BeginCooling";
	
	public BaseControllerLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
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
		double timespan = ((LinearFridge)fridge).tau(t_current, t_dest);
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
		double timespan = ((LinearFridge)fridge).tau(t_current, t_dest);
		// Remove any next scheduled cooling event if present
		interrupt(EV_BEGIN_COOLING);
		// Delay next cooling phase when warming is finished
		waitDelay(EV_BEGIN_COOLING, timespan, fridge.getT_min(), fridge
				.getQ_cooling());
	}
}