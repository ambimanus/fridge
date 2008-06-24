package de.uniol.ui.desync.model;

import java.util.ArrayList;

import simkit.SimEntityBase;
import de.uniol.ui.desync.model.controller.AbstractController;

public class ControlCenter extends SimEntityBase {

	public final static String EV_NOTIFY = "Notify";

	private ArrayList<AbstractController> controllers;
	private double t_notify;
	private double t_preload;
	private double spread;
	private boolean doUnload;

	public ControlCenter(ArrayList<AbstractController> controllers,
			double t_notify, double t_preload, double spread, boolean doUnload) {
		this.controllers = controllers;
		this.t_notify = t_notify;
		this.t_preload = t_preload;
		this.spread = spread;
		this.doUnload = doUnload;
	}

	public void doRun() {
		waitDelay(EV_NOTIFY, t_notify);
	}

	public void doNotify() {
		String ev = doUnload ? AbstractController.EV_UNLOAD_THERMAL_STORAGE
				: AbstractController.EV_LOAD_THERMAL_STORAGE;
		for (AbstractController c : controllers) {
			c.waitDelay(ev, 0, t_preload, spread);
		}
	}
}