package de.uniol.ui.desync.model.signals;

import simkit.SimEvent;
import de.uniol.ui.desync.model.controller.AbstractController;

public class SignalPerformerTimed extends AbstractSignalPerformer {

	public final static String EV_NOTIFY = "Notify";

	private double t_notify;
	private double tau_preload;
	private double tau_reduce;

	public SignalPerformerTimed(int eventListID, double t_notify, double tau_preload,
			double tau_reduce) {
		super(eventListID, "Signal: Timed Load Reduction");
		this.t_notify = t_notify;
		this.tau_preload = tau_preload;
		this.tau_reduce = tau_reduce;
	}

	public void handleEvent(SimEvent event) {
		if (EV_APPLY_TO_CONTROLLER.equals(event.getEventName())) {
			doApplyToController((AbstractController) event.getParameters()[0]);
		} else if (EV_NOTIFY.equals(event.getEventName())) {
			doNotify((AbstractController) event.getParameters()[0]);
		} else {
			error(event);
		}
	}

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Itlr)) {
			throw new IllegalArgumentException("Wrong controller type: " + c);
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		c.waitDelay(Itlr.EV_REDUCE_LOAD, 0, tau_preload,
				tau_reduce);
	}
}