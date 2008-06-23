package de.uniol.ui.model;

import java.util.ArrayList;

import de.uniol.ui.model.controller.DirectStorageControllerCompactLinear;

import simkit.SimEntityBase;

public class ControlCenter extends SimEntityBase {

	public final static String EV_NOTIFY = "Notify";
	
	private ArrayList<DirectStorageControllerCompactLinear> controllers;
	private double t_notify;
	private double t_preload;
	private boolean doUnload;
	
	public ControlCenter(
			ArrayList<DirectStorageControllerCompactLinear> controllers,
			double t_notify, double t_preload, boolean doUnload) {
		this.controllers = controllers;
		this.t_notify = t_notify;
		this.t_preload = t_preload;
		this.doUnload = doUnload;
	}
	
	public void doRun() {
		waitDelay(EV_NOTIFY, t_notify);
	}
	
	public void doNotify() {
		String ev = doUnload ? DirectStorageControllerCompactLinear.EV_UNLOAD_THERMAL_STORAGE
				: DirectStorageControllerCompactLinear.EV_LOAD_THERMAL_STORAGE;
		for (DirectStorageControllerCompactLinear c : controllers) {
			c.waitDelay(ev, 0, t_preload);
		}
	}
}