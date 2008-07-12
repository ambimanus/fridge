package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Itlr;

public class TimedControllerIterative extends BaseControllerIterative implements
		Itlr {
	
	public TimedControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		IterativeFridge f = (IterativeFridge) fridge;
		// Time to warmup from T_min to T_max
		double tau_warming = f
				.tau(f.getT_min(), f.getT_max(), f.getQ_warming());
		if (tau_warming < tau_reduce) {
			// We will not be able to survive tau_reduce, regardless of our
			// current temperature
			// TODO
			System.err.println("Cannot survive reduce interval: " + tau_reduce
					+ " > " + tau_warming);
			return;
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

		// Classify f to apply proper cooling program
		double t_current = f.getT_current();
		if (t_current > T_max_not) {
			// Class red: Fridge will not reach T_max_act
			// Cooldown till T_act
			waitDelay(EV_BEGIN_COOLING, 0.0, f.getQ_cooling());
			waitDelay(EV_BEGIN_WARMING, tau_preload, f.getQ_warming());
		} else if (t_current > T_mid_not) {
			// Class orange: Fridge can reach T_max_act, but not T_min
			// Cooldown till T_act
			waitDelay(EV_BEGIN_COOLING, 0.0, f.getQ_cooling());
			waitDelay(EV_BEGIN_WARMING, tau_preload, f.getQ_warming());
		} else if (t_current > T_min_not) {
			// Class green: Fridge can reach T_min
			// Cooldown to T_min
			waitDelay(EV_BEGIN_COOLING, 0.0, f.getQ_cooling());
		} else {
			// Class blue: Fridge does not need to cooldown any more
			// Cooldown to T_min
			waitDelay(EV_BEGIN_COOLING, 0.0, f.getQ_cooling());
		}
	}

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginCooling(java.lang.Double)
	 */
	@Override
	public void doBeginCooling(Double load) {
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		super.doBeginCooling(load);
	}

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginWarming(java.lang.Double)
	 */
	@Override
	public void doBeginWarming(Double load) {
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		super.doBeginWarming(load);
	}
}