package de.uniol.ui.desync.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class listens to value changes and calculates a progress based on a
 * predefined target value.
 * 
 * @author Chh
 */
public abstract class ProgressListener implements PropertyChangeListener {
	
	/** target value - used to calcualte progress */
	private double simulationLength;
	/** auxiliary variable - holds result from last change */
	private double percBackup = 0.0;
	
	/**
	 * Creates a new progress listener with the given target value.
	 * @param simulationLength
	 */
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
	
	/**
	 * This method is called each time the calculated progress changes.
	 * 
	 * @param progress
	 */
	protected abstract void progressChanged(int progress);
}
