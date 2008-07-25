package de.uniol.ui.desync.model.controller.extended;

public interface IStateful {

	public final static String EV_RESTORE_STATE = "RestoreState";
	
	public abstract void doRestoreState();
}