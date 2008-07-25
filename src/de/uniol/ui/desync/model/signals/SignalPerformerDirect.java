package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.controller.AbstractController;

public class SignalPerformerDirect extends AbstractSignalPerformer {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double spread;
	private boolean doUnload;

	public SignalPerformerDirect(int eventListID, double t_notify,
			double spread, boolean doUnload) {
		super(eventListID, "Signal: Direct Storage Control");
		this.t_notify = t_notify;
		this.spread = spread;
		this.doUnload = doUnload;
	}

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Idsc)) {
			System.err.println("Wrong controller type for signal DIRECT: '"
					+ c.getName() + '\'');
			return;
//			throw new IllegalArgumentException("Wrong controller type: "
//					+ c.getName());
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		String ev = doUnload ? Idsc.EV_UNLOAD_THERMAL_STORAGE
				: Idsc.EV_LOAD_THERMAL_STORAGE;
		c.waitDelay(ev, 0, spread);
	}
}