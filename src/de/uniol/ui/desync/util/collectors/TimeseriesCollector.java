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
 * @author Chh
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
}