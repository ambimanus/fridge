package de.uniol.ui.desync.util.collectors;


public abstract class AbstractCollector {
	
	protected String name;
	
	public AbstractCollector(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract double[][] getResults();
}