package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.controller.AbstractController;

public class StrategyPerformerDirect extends AbstractStrategyPerformer {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double spread;
	private boolean doUnload;

	public StrategyPerformerDirect(int eventListID, double t_notify,
			double spread, boolean doUnload) {
		super(eventListID, "Strategy; Direct Storage Control");
		this.t_notify = t_notify;
		this.spread = spread;
		this.doUnload = doUnload;
	}

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Idsc)) {
			throw new IllegalArgumentException("Wrong controller type: " + c);
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		String ev = doUnload ? Idsc.EV_UNLOAD_THERMAL_STORAGE
				: Idsc.EV_LOAD_THERMAL_STORAGE;
		c.waitDelay(ev, 0, spread);
	}
}