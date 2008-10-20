package de.uniol.ui.desync.model.signals;

import de.uniol.ui.desync.model.controller.AbstractController;

/**
 * Signal performer which sends the TLR signal to controllers.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
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

	public void doApplyToController(AbstractController c) {
		if (!(c instanceof Itlr)) {
			System.err.println("Wrong controller type for signal TIMED: \""
					+ c.getName() + '"');
			return;
//			throw new IllegalArgumentException("Wrong controller type: "
//					+ c.getName());
		}
		waitDelay(EV_NOTIFY, t_notify, c);
	}

	public void doNotify(AbstractController c) {
		c.waitDelay(Itlr.EV_REDUCE_LOAD, 0, tau_preload,
				tau_reduce);
	}
}