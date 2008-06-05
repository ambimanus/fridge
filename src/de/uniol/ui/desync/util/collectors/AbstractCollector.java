package de.uniol.ui.desync.util.collectors;

import java.beans.PropertyChangeListener;

public abstract class AbstractCollector implements PropertyChangeListener {
	
	protected int list;
	protected String name;
	
	public AbstractCollector(int eventListId, String name) {
		this.list = eventListId;
		this.name = name;
	}
	
	public int getEventListId() {
		return list;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract double[][] getResults();
}