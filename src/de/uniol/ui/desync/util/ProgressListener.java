package de.uniol.ui.desync.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class ProgressListener implements PropertyChangeListener {
	
	private double simulationLength;
	private double percBackup = 0.0;
	
	public ProgressListener(double simulationLength) {
		this.simulationLength = simulationLength;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		Double perc = (Double) evt.getOldValue() * 100d / simulationLength;
		if (Math.floor(perc) != Math.floor(percBackup)) {
			percBackup = perc;
			progressChanged((int)Math.floor(perc));
		}
		if ((Double) evt.getNewValue() == simulationLength) {
			progressChanged(100);
		}
	}
	
	protected abstract void progressChanged(int progress);
}
