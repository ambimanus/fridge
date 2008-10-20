package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.controller.AbstractController;

/**
 * This abstract class defines a signal performer which sends a specific control
 * signal to a given controller. Thsi is a SimEntity.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public abstract class AbstractSignalPerformer extends SimEntityClean {

	public final static String EV_APPLY_TO_CONTROLLER = "ApplyToController";
	
	public AbstractSignalPerformer(int eventListID, String name) {
		super(eventListID);
		setName(name);
	}
	
	/**
	 * Event: Sends the contained signal to the given controller.
	 * @param c
	 */
	public abstract void doApplyToController(AbstractController c);
}