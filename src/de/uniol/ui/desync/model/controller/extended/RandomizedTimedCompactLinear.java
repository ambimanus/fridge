package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;

/**
 * This controller extension adds the randomized damping mode to a
 * {@link TimedControllerCompactLinear}.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class RandomizedTimedCompactLinear extends
		TimedControllerCompactLinear implements IRandomized {
	
	public RandomizedTimedCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		super.doReduceLoad(tau_preload, tau_reduce);
		waitDelay(EV_RANDOMIZE_ACTION, tau_preload + tau_reduce);
	}
	
	public void doRandomizeAction() {
		// Signal action has been performed, now randomize further behaviour by
		// targeting a random temperature in [Tmin,Tmax].
		double t_dest = drawUniformRandom(fridge.getT_min(), fridge.getT_max());
		if (t_dest > fridge.getT_current()) {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_warming());
		} else {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, t_dest, fridge.getQ_cooling());
		}
	}
}