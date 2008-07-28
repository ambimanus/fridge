package de.uniol.ui.desync.model.controller;

import java.util.ArrayList;
import java.util.Collections;

import de.uniol.ui.desync.model.controller.TimedClassifier.classes;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Itlr;

public class TimedControllerIterative extends BaseControllerIterative implements
		Itlr {
	
	public final static String EV_DELAY_WARMING = "DelayWarming";
	
	public TimedControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		// Classify f to apply proper cooling program
		double now = getEventList().getSimTime();
		TimedClassifier tc = new TimedClassifier(fridge);
		classes c = tc.classifyFridge(now, tau_preload, tau_reduce);
		switch (c) {
		case BLACK: {
			System.err.println(getName()
					+ ": class BLACK, this should not happen.");
			break;
		}
		case BROWN: {
			// Find first field starting in the future (except the red one)
			ArrayList<Double> fields = new ArrayList<Double>();
			fields.add(tc.getTA());
			fields.add(tc.getTB());
			fields.add(tc.getTC());
			fields.add(tc.getTD());
			Collections.sort(fields);
			int i = 0;
			double firstField = fields.get(i);
			while (firstField <= now && i < 2) {
				i++;
				firstField = fields.get(i);
			}
			double delay = firstField - now;
			if (delay > 0.0 && delay < tau_preload) {
				// If a field has been found, which begins in future time, and
				// before tAct, schedule this method again at that point in
				// time.
				waitDelay(EV_REDUCE_LOAD, delay, tau_preload - delay,
						tau_reduce);
			} else {
				// The fields GREEN, BLUE and ORANGE started in the past, so the
				// best would be to immediately start cooling as much as
				// possible.
				double t_current = fridge.getT_current();
				if (fridge.tau(t_current, fridge.getT_min(), fridge
						.getQ_cooling()) <= tau_preload) {
					// Enough time to cool to t_min:
					// Cooldown to T_min
					waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
				} else {
					// Not enough time to cool to t_min. Check if it's okay to
					// cool till t_act:
					double t_dest = fridge.calculateTemperatureAfter(true,
							tau_preload, t_current, fridge.getQ_cooling());
					if (t_dest >= fridge.getT_min()) {
						// Cooldown till t_act
						waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
						waitDelay(EV_DELAY_WARMING, tau_preload, fridge
								.getQ_warming());
					} else {
						// Nothing can be done here, just go on with regular
						// cooling program.
						System.err.println(getName()
								+ " - cannot apply a cooling program.");
					}
				}
			}
			break;
		}
		case RED: {
			// Cooldown till t_act
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
			waitDelay(EV_DELAY_WARMING, tau_preload, fridge.getQ_warming());
			break;
		}
		case ORANGE: {
			// Cooldown till t_act
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
			waitDelay(EV_DELAY_WARMING, tau_preload, fridge.getQ_warming());
			break;
		}
		case GREEN: {
			// Cooldown to T_min
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
			break;
		}
		case BLUE: {
			// Cooldown to T_min
			waitDelay(EV_BEGIN_COOLING, 0.0, fridge.getQ_cooling());
			break;
		}
		}
	}
	
	public void doDelayWarming(Double load) {
		waitDelay(EV_BEGIN_WARMING, 0.0, load);
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