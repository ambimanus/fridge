package de.uniol.ui.desync.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import de.uniol.ui.model.Fridge;

import simkit.Schedule;
import simkit.stat.SimpleStatsTally;

public class XYCollector implements PropertyChangeListener {

	private int list;
	private double lastSimTime = 0.0;
	private ArrayList<Double> times = new ArrayList<Double>();
	private ArrayList<Double> values = new ArrayList<Double>();
	private SimpleStatsTally sst = new SimpleStatsTally(Fridge.PROP_TEMPERATURE);

	public XYCollector(int eventListId) {
		this.list = eventListId;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (lastSimTime != Schedule.getEventList(list).getSimTime()) {
			times.add(lastSimTime);
			values.add(sst.getMean());
			sst.reset();
			lastSimTime = Schedule.getEventList(list).getSimTime();
		}
		sst.newObservation((Double) evt.getNewValue());
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
}