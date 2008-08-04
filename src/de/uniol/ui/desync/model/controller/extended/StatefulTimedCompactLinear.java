package de.uniol.ui.desync.model.controller.extended;

import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.fridges.State;
import de.uniol.ui.desync.util.Geometry;

public class StatefulTimedCompactLinear extends
		TimedControllerCompactLinear implements IStateful {

	protected final static String EV_CALCULATE_ACTION = "CalculateAction";
	protected final static String EV_CALCULATE_STATE = "CalculateState";
	
	protected State originalState;
	protected State regularState;
	protected State desiredState;

	public StatefulTimedCompactLinear(LinearFridge fridge,
			int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		super.doReduceLoad(tau_preload, tau_reduce);
		// Memorize current (original) state of the fridge
		originalState = new State(fridge);
		// Schedule calculation of state restoration plan at t_activ
		waitDelay(EV_CALCULATE_ACTION, tau_preload, tau_reduce);
	}
	
	public void doCalculateAction(Double tau_reduce) {
		// Calculate the optimal point in time when to restore the regular state
		double warmingEnd = fridge.tau(fridge.getT_current(),
				fridge.getT_max(), fridge.getQ_warming());
		if (warmingEnd < tau_reduce) {
			// If we have to cooldown before the end of the reduce interval,
			// schedule the state restoration at that point
			waitDelay(EV_CALCULATE_STATE, warmingEnd);
			// Calculate and memorize state of regular fridge at that point
			regularState = fridge.calculateStateAfterLongRun(originalState,
					warmingEnd);
		} else {
			// If we can survive tau_reduce, schedule the state restoration
			// immediately after the reduce interval
			waitDelay(EV_CALCULATE_STATE, tau_reduce);
			// Calculate and memorize state of regular fridge at that point
			regularState = fridge.calculateStateAfterLongRun(originalState,
					tau_reduce);
		}
	}
	
	public void doCalculateState() {
		State state = new State(fridge);
		// Only proceed if a modification has occured
		if (!state.equals(regularState)) {
			double now = getEventList().getSimTime();
			// Find nearest crossing of the modified and regular curves by
			// sequentially checking the intersection of consecutive line
			// segments
			double[] is = new double[2];
			int ret;
			// Create line segments
			Line mod = new Line();
			Line reg = new Line();
			Line modNext = new Line();
			Line regNext = new Line();
			mod.x1 = now;
			mod.y1 = state.t;
			if (state.active) {
				mod.x2 = mod.x1
						+ fridge.tau(state.t, fridge.getT_min(), fridge
								.getQ_cooling());
				mod.y2 = fridge.getT_min();
				modNext.x1 = mod.x2;
				modNext.y1 = mod.y2;
				modNext.x2 = modNext.x1
						+ fridge.tau(modNext.y1, fridge.getT_max(), fridge
								.getQ_warming());
				modNext.y2 = fridge.getT_max();
			} else {
				mod.x2 = mod.x1
						+ fridge.tau(state.t, fridge.getT_max(), fridge
								.getQ_warming());
				mod.y2 = fridge.getT_max();
				modNext.x1 = mod.x2;
				modNext.y1 = mod.y2;
				modNext.x2 = modNext.x1
						+ fridge.tau(modNext.y1, fridge.getT_min(), fridge
								.getQ_cooling());
				modNext.y2 = fridge.getT_min();
			}
			reg.x1 = now;
			reg.y1 = regularState.t;
			if (regularState.active) {
				reg.x2 = reg.x1
						+ fridge.tau(regularState.t, fridge.getT_min(), fridge
								.getQ_cooling());
				reg.y2 = fridge.getT_min();
				regNext.x1 = reg.x2;
				regNext.y1 = reg.y2;
				regNext.x2 = regNext.x1
						+ fridge.tau(regNext.y1, fridge.getT_max(), fridge
								.getQ_warming());
				regNext.y2 = fridge.getT_max();
			} else {
				reg.x2 = reg.x1
						+ fridge.tau(regularState.t, fridge.getT_max(), fridge
								.getQ_warming());
				reg.y2 = fridge.getT_max();
				regNext.x1 = reg.x2;
				regNext.y1 = reg.y2;
				regNext.x2 = regNext.x1
						+ fridge.tau(regNext.y1, fridge.getT_min(), fridge
								.getQ_cooling());
				regNext.y2 = fridge.getT_min();
			}
			// Check intersection of mod & reg
			ret = Geometry.findLineSegmentIntersection(mod.x1, mod.y1, mod.x2,
					mod.y2, reg.x1, reg.y1, reg.x2, reg.y2, is);
			if (ret != 1) {
				// Check intersection of mod & regNext
				ret = Geometry.findLineSegmentIntersection(mod.x1, mod.y1,
						mod.x2, mod.y2, regNext.x1, regNext.y1, regNext.x2,
						regNext.y2, is);
			}
			if (ret != 1) {
				// Check intersection of modNext & reg
				ret = Geometry.findLineSegmentIntersection(modNext.x1,
						modNext.y1, modNext.x2, modNext.y2, reg.x1, reg.y1,
						reg.x2, reg.y2, is);
			}
			if (ret != 1) {
				// Check intersection of modNext & regNext
				ret = Geometry.findLineSegmentIntersection(modNext.x1,
						modNext.y1, modNext.x2, modNext.y2, regNext.x1,
						regNext.y1, regNext.x2, regNext.y2, is);
			}
			if (ret != 1) {
				// Should not happen:
				System.err.println(getName() + " - No crossing point found: "
						+ state + " vs. " + regularState);
			}
			// Check rounding error (negative delay)
			if (Geometry.equals(is[0], now)) {
				desiredState = regularState;
				waitDelay(EV_RESTORE_STATE, 0.0);
			} else {
				// Determine the desired state at the calculated point in time
				desiredState = fridge.calculateStateAfterLongRun(regularState,
						is[0] - now);
				// Schedule state restore at the calculated point in time
				waitDelay(EV_RESTORE_STATE, is[0] - now);
			}
		}
	}

	public void doRestoreState() {
		// Cancel pending cooling program
		interruptAll(EV_TARGET_TO);
		// Apply calculated state
		waitDelay(EV_TARGET_TO, 0.0, desiredState.active ? fridge.getT_min()
				: fridge.getT_max(), desiredState.q);
	}
}