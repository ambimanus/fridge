package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import simkit.EventList;
import simkit.SimEntity;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.desync.util.MessagingEventList;

/**
 * This collector collects data from multiple {@link SimEntity}s and calculates
 * the statistical mean of the observed values.
 * 
 * @author Chh
 */
public class TimeseriesMultiMeanCollector extends AbstractCollector {

	/** Observed entities */
	protected HashMap<SimEntity, Double> entities = new HashMap<SimEntity, Double>();
	/** Tally to calculate mean */
	protected SimpleStatsTally sst;
	/** defines whether a value has changed in the last interval */
	protected boolean changed = false;
	
	/** holds the timestamps (x-values) */
	protected ArrayList<Double> times = new ArrayList<Double>();
	/** holds the collected values (y-values) */
	protected ArrayList<Double> values = new ArrayList<Double>();
	
	/**
	 * Creates a new {@link TimeseriesMultiMeanCollector} listening to simTime
	 * progress in the given {@link EventList}. Observed {@link SimEntity}s
	 * are added later on.
	 * 
	 * @param eventlist
	 * @param name
	 */
	public TimeseriesMultiMeanCollector(MessagingEventList eventlist, String name) {
		super(name);
		eventlist.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				nextInterval((Double) evt.getOldValue(), (Double) evt
								.getNewValue());
			}
		});
		sst = new SimpleStatsTally();
	}
	
	/**
	 * Adds a new {@link SimEntity} and a corresponding property identifier to
	 * be observed.
	 * 
	 * @param entity
	 * @param property
	 */
	public void addEntity(SimEntity entity, String property) {
		entities.put(entity, Double.NaN);
		entity.addPropertyChangeListener(property, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SimEntity entity = (SimEntity) evt.getSource();
				entities.put(entity, (Double) evt.getNewValue());
				changed = true;
			}
		});
	}
	
	/**
	 * Called when the simTime of the event list proceeds. If values in the
	 * observed entities had changed since the last inverall, a new mean value
	 * is calculated and added to the data points.
	 * 
	 * @param oldSimtime
	 * @param newSimtime
	 */
	protected void nextInterval(double oldSimtime, double newSimtime) {
		if (changed) {
			sst.reset();
			for (SimEntity se : entities.keySet()) {
				sst.newObservation(entities.get(se));
			}
			addObservation(oldSimtime, sst.getMean());
		}
		changed = false;
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