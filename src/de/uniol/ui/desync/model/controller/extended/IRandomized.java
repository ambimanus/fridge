package de.uniol.ui.desync.model.controller.extended;

/**
 * This interface defines the randomized damping strategy.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public interface IRandomized {

	public final static String EV_RANDOMIZE_ACTION = "RandomizeAction";
	
	public abstract void doRandomizeAction();
}