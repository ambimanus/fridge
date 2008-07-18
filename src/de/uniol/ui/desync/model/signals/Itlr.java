package de.uniol.ui.desync.model.signals;

public interface Itlr {

	public final static String EV_REDUCE_LOAD = "ReduceLoad";
	public static enum classes {
		BLACK, RED, ORANGE, GREEN, BLUE
	}

	public abstract void doReduceLoad(Double tau_preload, Double tau_reduce);
}