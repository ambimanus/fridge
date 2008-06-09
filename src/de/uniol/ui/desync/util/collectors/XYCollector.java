package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class XYCollector extends AbstractCollector {

	protected double lastSimTime = 0.0;
	protected ArrayList<Double> times = new ArrayList<Double>();
	protected ArrayList<Double> values = new ArrayList<Double>();

	public XYCollector(int eventListId, String name) {
		super(name);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (!(evt.getNewValue() instanceof Double)) {
			throw new IllegalArgumentException("Expected Double, but got "
					+ evt.getNewValue().getClass());
		}
		times.add(lastSimTime);
		values.add((Double)evt.getNewValue());
//		if (Fridge.PROP_LOAD.equals(sst.getName())) {
//			System.out.println(getName() + ": " + lastSimTime + " - "
//					+ sst.getMean() + " W");
//		}
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