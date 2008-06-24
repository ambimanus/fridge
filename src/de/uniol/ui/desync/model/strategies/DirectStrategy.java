package de.uniol.ui.desync.model.strategies;

import de.uniol.ui.desync.model.controller.AbstractController;
import de.uniol.ui.desync.model.controller.DirectAbstractController;

public class DirectStrategy extends AbstractStrategy {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double t_preload;
	private double spread;
	private boolean doUnload;

	public DirectStrategy(int eventListID, double t_notify, double t_preload,
			double spread, boolean doUnload) {
		super(eventListID, "Strategy; Direct Storage Control");
		this.t_notify = t_notify;
		this.t_preload = t_preload;
		this.spread = spread;
		this.doUnload = doUnload;
	}

	public void doApplyToController(AbstractController c) {
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		String ev = doUnload ? DirectAbstractController.EV_UNLOAD_THERMAL_STORAGE
				: DirectAbstractController.EV_LOAD_THERMAL_STORAGE;
		c.waitDelay(ev, 0, t_preload, spread);
	}
}