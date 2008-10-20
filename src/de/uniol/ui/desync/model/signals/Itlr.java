package de.uniol.ui.desync.model.signals;

/**
 * This interface defines the TLR control signal.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public interface Itlr {

	public final static String EV_REDUCE_LOAD = "ReduceLoad";

	public abstract void doReduceLoad(Double tau_preload, Double tau_reduce);
}