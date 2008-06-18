package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeListener;

/**
 * This abstract class defines base maethods which must be implemented by all
 * statistical collectors in this software. The collectors are implemented as
 * {@link PropertyChangeListener}s (or internally use them) to collect the data
 * from the simulation model.
 * 
 * @author Chh
 */
public abstract class AbstractCollector {
	
	/** Name of this collector, for displaying purposes only */
	protected String name;
	
	/**
	 * Creates a new collector with the given name.
	 * 
	 * @param name
	 */
	public AbstractCollector(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name of this collector
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a new data point defined by the given time (x) and value
	 * (y) params.
	 * 
	 * @param time
	 * @param value
	 */
	public abstract void addObservation(double time, double value);
	
	/**
	 * @return the collected data as twodimensional array
	 */
	public abstract double[][] getResults();
	
	/**
	 * @return amount of collected data points
	 */
	public abstract int getSize();
	
	/**
	 * @param index
	 * @return data point at the specified index of the internal array (not
	 *         time point!)
	 */
	public abstract double[] getObservation(int index);
}