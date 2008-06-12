package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import simkit.EventList;
import simkit.SimEntity;

public class TimeseriesCollector extends AbstractCollector {

	protected ArrayList<Double> times = new ArrayList<Double>();
	protected ArrayList<Double> values = new ArrayList<Double>();
	
	public TimeseriesCollector(EventList eventlist, String name, SimEntity entity, String property) {
		super(name);
		final EventList el = eventlist;
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
			t[i] = times.get(i);
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