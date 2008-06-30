package de.uniol.ui.desync.model.strategies;

public interface IStrategyTimed {

	public final static String EV_REDUCE_LOAD = "ReduceLoad";

	public abstract void doReduceLoad(Double tau_preload, Double tau_reduce);
}