package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Itlr;

public class TimedControllerCompactLinear extends BaseControllerCompactLinear
		implements Itlr {
	
	public TimedControllerCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	protected classes classifyFridge(Double tau_preload, Double tau_reduce) {
		AbstractFridge f = (AbstractFridge) fridge;
		// Time to warmup from T_min to T_max
		double tau_warming = f
				.tau(f.getT_min(), f.getT_max(), f.getQ_warming());
		if (tau_warming < tau_reduce) {
			// We will not be able to survive tau_reduce, regardless of our
			// current temperature
// return classes.BLACK;
		}
		// Time to cooldown from T_max to T_min
		double tau_cooling = f
				.tau(f.getT_max(), f.getT_min(), f.getQ_cooling());
		// Time to warmup from T_min to T_max_act (this allows us to calculate
		// T_max_act)
		double tau_T_min_to_T_max_act = tau_warming - tau_reduce;
		// Maximal temperature after tau_preload to extactly fit into tau_reduce
		double T_max_act = f.calculateTemperatureAfter(tau_T_min_to_T_max_act,
				f.getT_min(), f.getQ_warming());
		// Time to cooldown from T_max to T_allowed_act
		double tau_T_max_to_T_max_act = f.tau(f.getT_max(), T_max_act, f
				.getQ_cooling());
		// Time to cooldown from T_max to T_max_not (this allows us to calculate
		// T_max_not)
		double tau_T_max_to_T_max_not = tau_T_max_to_T_max_act - tau_preload;
		// Maximal temperature at T_notify to reach T_max_act
		double T_max_not = f.calculateTemperatureAfter(tau_T_max_to_T_max_not,
				f.getT_max(), f.getQ_cooling());
		// Maximal temperature at T_notify to reach T_min after tau_preload
		double T_mid_not = f.calculateTemperatureAfter(tau_cooling
				- tau_preload, f.getT_max(), f.getQ_cooling());
		// Maximal temperature at T_notify to survive tau_reduce without preload
		double T_min_not = f.calculateTemperatureAfter(tau_T_min_to_T_max_act
				- tau_preload, f.getT_min(), f.getQ_warming());

		double t_current = f.getT_current();
		if (t_current > T_max_not) {
			// Class red: Fridge will not reach T_max_act
			return classes.RED;
		} else if (t_current > T_mid_not) {
			// Class orange: Fridge can reach T_max_act, but not T_min
			return classes.ORANGE;
		} else if (t_current > T_min_not) {
			// Class green: Fridge can reach T_min
			return classes.GREEN;
		} else {
			// Class blue: Fridge does not need to cooldown any more
			return classes.BLUE;
		}
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		// Cancel pending cooling program
		interruptAll(EV_TARGET_TO);
		// Classify f to apply proper cooling program
		classes c = classifyFridge(tau_preload, tau_reduce);
		switch (c) {
		case BLACK: {
			// TODO
			break;
		}
		case RED: {
			// Cooldown till t_act
			double t_current = fridge.getT_current();
			waitDelay(EV_TARGET_TO, 0.0, fridge.calculateTemperatureAfter(
					tau_preload, t_current, fridge.getQ_cooling()), fridge
					.getQ_cooling());
			break;
		}
		case ORANGE: {
			// Cooldown till t_act
			double t_current = fridge.getT_current();
			waitDelay(EV_TARGET_TO, 0.0, fridge.calculateTemperatureAfter(
					tau_preload, t_current, fridge.getQ_cooling()), fridge
					.getQ_cooling());
			break;
		}
		case GREEN: {
			// Cooldown to T_min
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
			break;
		}
		case BLUE: {
			// Cooldown to T_min
			waitDelay(EV_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
			break;
		}
		}
	}
}