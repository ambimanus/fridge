/*
 * AdaptiveFridge Copyright (C) 2008 Christian Hinrichs
 * 
 * AdaptiveFridge is copyright under the GNU General Public License.
 * 
 * This file is part of AdaptiveFridge.
 * 
 * AdaptiveFridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AdaptiveFridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AdaptiveFridge.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniol.ui.desync.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class listens to value changes and calculates a progress based on a
 * predefined target value.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */
public abstract class ProgressListener implements PropertyChangeListener {
	
	/** target value - used to calcualte progress */
	private double simulationLength;
	/** auxiliary variable - holds result from last change */
	private double percBackup = 0.0;
	/** current progress */
	protected Integer progress = 0;
	
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
	 * @return the current progress
	 */
	public int getProgress() {
		synchronized (progress) {
			return progress;
		}
	}
	
	/**
	 * This method is called each time the calculated progress changes.
	 * 
	 * @param progress
	 */
	protected abstract void progressChanged(int progress);
	
	
}
