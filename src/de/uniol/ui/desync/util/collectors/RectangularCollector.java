package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import de.uniol.ui.model.Fridge;

import simkit.Schedule;
import simkit.stat.SimpleStatsTally;

public class RectangularCollector extends AbstractCollector {

	protected double lastSimTime = 0.0;
	protected ArrayList<Double> times = new ArrayList<Double>();
	protected ArrayList<Double> values = new ArrayList<Double>();
	protected SimpleStatsTally sst = new SimpleStatsTally(Fridge.PROP_TEMPERATURE);
	
	public RectangularCollector(int eventListId, String name) {
		super(eventListId, name);
	}
	
	/*
	 * Override to add each datapoint twice: one at the height of the last
	 * point, and one at the new height. This will result in a rectangle curve
	 * that is characteristical for load curves.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (lastSimTime != Schedule.getEventList(list).getSimTime()) {
			// Add first (backup) point
			times.add(lastSimTime);
			if (!values.isEmpty()) {
				values.add(values.get(values.size()-1));
			} else {
				values.add(0.0);
			}
			// Add second (actual) point
			times.add(lastSimTime);
			values.add(sst.getMean());
			sst.reset();
			lastSimTime = Schedule.getEventList(list).getSimTime();
		}
		sst.newObservation((Double) evt.getNewValue());
	}

	public double[][] getResults() {
		// Add last observed values
		//1: Backup point for last measure
		times.add(lastSimTime);
		if (!values.isEmpty()) {
			values.add(values.get(values.size()-1));
		} else {
			values.add(0.0);
		}
		//2: Actual measure
		times.add(lastSimTime);
		values.add(sst.getMean());
		//3: Endpoint with same value (like a future-backup point)
		times.add(Schedule.getEventList(list).getSimTime());
		values.add(sst.getMean());
		// Generate result arrays
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
}
