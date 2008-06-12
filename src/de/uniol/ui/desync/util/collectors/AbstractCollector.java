package de.uniol.ui.desync.util.collectors;

public abstract class AbstractCollector {
	
	protected String name;
	
	public AbstractCollector(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void addObservation(double time, double value);
	
	public abstract double[][] getResults();
	
	public abstract int getSize();
	
	public abstract double[] getObservation(int index);
}