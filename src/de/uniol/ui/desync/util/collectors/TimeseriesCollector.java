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
package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import simkit.EventList;
import simkit.SimEntity;

/**
 * This collector just collects simple data points with their occurence
 * timestamps.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */
public class TimeseriesCollector extends AbstractCollector {

	/** holds the timestamps (x-values) */
	protected ArrayList<Double> times = new ArrayList<Double>();
	/** holds the collected values (y-values) */
	protected ArrayList<Double> values = new ArrayList<Double>();
	
	/**
	 * Creates a new {@link TimeseriesCollector} listening to the specified
	 * property in the given {@link SimEntity}.
	 * 
	 * @param name
	 * @param entity
	 * @param property
	 */
	public TimeseriesCollector(String name, SimEntity entity, String property) {
		super(name);
		final EventList el = entity.getEventList();
		entity.addPropertyChangeListener(property,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						addObservation(el.getSimTime(), (Double) evt.getNewValue());
					}
				});
	}

	public void addObservation(double time, double value) {
		times.add(time);
		values.add(value);
	}
	
	public double[][] getResults() {
		double[] t = new double[times.size()];
		double[] v = new double[values.size()];
		if (t.length != v.length) {
			throw new RuntimeException("Different length of x/y arrays!");
		}
		for (int i = 0; i < t.length; i++) {
			// Multiply by 60000 to convert to milliseconds (used in visualizing
			// charts)
			t[i] = times.get(i) * 60000d;
			v[i] = values.get(i);
		}
		return new double[][] { t, v };
	}

	public double[] getObservation(int index) {
		return new double[] { times.get(index), values.get(index) };
	}

	public int getSize() {
		return times.size();
	}

	public void clear() {
		times.clear();
		values.clear();
	}
}