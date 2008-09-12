package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Itlr;

public class TimedControllerIterative extends BaseControllerIterative implements
		Itlr {
	
	public final static String EV_DELAY_WARMING = "DelayWarming";
	
	public TimedControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		// FIXME implement
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