package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import simkit.Schedule;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.model.Fridge;

public class LinearCollector extends AbstractCollector {

	protected double lastSimTime = 0.0;
	protected ArrayList<Double> times = new ArrayList<Double>();
	protected ArrayList<Double> values = new ArrayList<Double>();
	protected SimpleStatsTally sst = new SimpleStatsTally(Fridge.PROP_TEMPERATURE);

	public LinearCollector(int eventListId, String name) {
		super(eventListId, name);
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