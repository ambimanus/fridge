package de.uniol.ui.desync.model;

import java.util.ArrayList;

import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.signals.AbstractSignalPerformer;

/**
 * This class represents a control center which produces the control signals and
 * sends them to the controllers. This is modelled as SimEntity.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class ControlCenter extends SimEntityClean {

	protected ArrayList<AbstractFridge> fridges;
	protected AbstractSignalPerformer strategy;

	public ControlCenter(int eventListID, ArrayList<AbstractFridge> fridges,
			AbstractSignalPerformer strategy) {
		super(eventListID);
		this.fridges = fridges;
		this.strategy = strategy;
	}

	public void doRun() {
		for (AbstractFridge f : fridges) {
			strategy.waitDelay(
					AbstractSignalPerformer.EV_APPLY_TO_CONTROLLER, 0.0, f
							.getController());
		}
	}
}