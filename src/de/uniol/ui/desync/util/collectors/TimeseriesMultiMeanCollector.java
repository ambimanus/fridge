package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import simkit.EventList;
import simkit.SimEntity;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.util.MessagingEventList;

/**
 * This collector collects data from multiple {@link SimEntity}s and calculates
 * the statistical mean of the observed values.
 * 
 * @author Chh
 */
public class TimeseriesMultiMeanCollector extends AbstractCollector {

	/** underlying FEL */
	protected MessagingEventList eventlist;
	/** Observed entities */
	protected HashMap<SimEntity, Double> entities = new HashMap<SimEntity, Double>();
	/** Tally to calculate mean */
	protected SimpleStatsTally sst;
	/** defines whether a value has changed in the last interval */
	protected boolean changed = false;
	/** Stats to calculate time varying statistic values */
	protected SimpleStatsTimeVarying sstv;
	/** Timestamp of last oservation */
	protected double oldSimTime;
	/** the actual listener */
	protected PropertyChangeListener listener;
	
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
		this.eventlist = eventlist;
		eventlist.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				nextInterval((Double) evt.getOldValue());
			}
		});
		// Observe FEL for simulation stop
		PropertyChangeListener stopListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				nextInterval(TimeseriesMultiMeanCollector.this.eventlist
						.getSimTime());
			}
		};
		eventlist.addPropertyChangeListener(MessagingEventList.PROP_STOPPING,
				stopListener);
		oldSimTime = eventlist.getSimTime();
		sst = new SimpleStatsTally();
		sstv = new SimpleStatsTimeVarying() {
			/* (non-Javadoc)
			 * @see simkit.stat.SimpleStatsTimeVarying#newObservation(double)
			 */
			@Override
			public void newObservation(double x) {
				// Code from AbstractSimpleStats#newObservation(double x)
				count++;
				if (x < minObs) { minObs = x; }
		        if (x > maxObs) { maxObs = x; }
				// Code from SimpleStatsTimeVarying#newObservation(double x)
		        if (count == 1 ) {
		            mean = diff;
		            variance = 0.0;
		        } else if (oldSimTime > lastTime) {
		            double factor = 1.0 - (lastTime - getStartTime()) / (oldSimTime - this.getStartTime());
		            mean += diff * factor;
		            variance +=  factor * ( (1.0 - factor) * diff * diff - variance );
		        }
		        diff = x - mean;
		        this.lastTime = oldSimTime;
			}
			/* (non-Javadoc)
			 * @see simkit.stat.AbstractSimpleStats#toString()
			 */
			@Override
			public String toString() {
		        StringBuffer buf = new StringBuffer();
		        buf.append(getName());
		        buf.append(' ');
		        buf.append('(');
		        buf.append(this.getSamplingType());
		        buf.append(")");
		        buf.append(EOL);
		        buf.append("\t# | min | max | mean | variance | std.dev" + EOL + '\t');
		        return buf.toString() + getDataLine();
		    }
		};
		sstv.setEventListID(eventlist.getID());
		sstv.reset();
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
		if (listener == null) {
			listener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					SimEntity entity = (SimEntity) evt.getSource();
					double val = (Double) evt.getNewValue();
					entities.put(entity, val);
//					/*
//					 * The following snippet checks for errors in the model. It
//					 * prints an error message whenever a temperature value
//					 * outside its allowed bounds [t_min, t_max] is detected.
//					 */
//					if (AbstractFridge.PROP_TEMPERATURE.equals(evt
//							.getPropertyName())) {
//						AbstractFridge f = (AbstractFridge) evt.getSource();
//						float t = (float) val;
//						if (t < f.getT_min() || t > f.getT_max()) {
//							System.err.println(eventlist.getSimTime()
//									+ " ERROR - temperature out of range: "
//									+ f.getName() + ", t=" + t);
//						}
//					}
// TODO error checking
					changed = true;
				}
			};
		}
		entity.addPropertyChangeListener(property, listener);
	}
	
	private boolean zero = false;
	private double start;
	
	/**
	 * Called when the simTime of the event list proceeds. If values in the
	 * observed entities had changed since the last inverall, a new mean value
	 * is calculated and added to the data points.
	 * 
	 * @param oldSimtime
	 */
	protected void nextInterval(double oldSimTime) {
		this.oldSimTime = oldSimTime;
		if (changed) {
			sst.reset();
			for (SimEntity se : entities.keySet()) {
				double val = entities.get(se);
				sst.newObservation(val);
			}
			double mean = sst.getMean();
//if (!zero && mean == 0.0) {
//	System.out.println("start=" + oldSimTime);
//	start = oldSimTime;
//	zero = true;
//}
//if (zero && mean > 0.0) {
//	System.out.println("end=" + oldSimTime);
//	System.out.println("\tdur=" + (oldSimTime - start));
//	zero = false;
//}
// TODO q=0 logging
			addObservation(oldSimTime, mean);
			sstv.newObservation(mean);
		}
		changed = false;
	}

	public void addObservation(double time, double value) {
		times.add(time);
		values.add(value);
	}
	
	public ArrayList<Double> getTimes() {
		return times;
	}

	public ArrayList<Double> getValues() {
		return values;
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
	
	public SimpleStatsTimeVarying getTimeVaryingStats() {
		return sstv;
	}

	public void clear() {
		times.clear();
		values.clear();
		sst.reset();
		sstv.reset();
	}
}