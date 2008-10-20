package de.uniol.ui.desync.model.controller.extended;

/**
 * This interface defines the stateful damping strategy.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public interface IStateful {

	public final static String EV_RESTORE_STATE = "RestoreState";
	
	public abstract void doRestoreState();
}