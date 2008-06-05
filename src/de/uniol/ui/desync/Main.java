package de.uniol.ui.desync;

import java.util.ArrayList;

import simkit.Schedule;
import de.uniol.ui.desync.ui.LoadChart;
import de.uniol.ui.desync.ui.ProgressComposite;
import de.uniol.ui.desync.ui.TemperatureChart;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ProgressListener;
import de.uniol.ui.desync.util.VarianceGenerator;
import de.uniol.ui.desync.util.XYCollector;
import de.uniol.ui.model.Fridge;

public class Main {

	public final static int POPULATION_SIZE = 5000;
	public final static double MAX_VARIANCE = 0.2;
	public final static int MAX_VARIANCE_FRACTION = 2;
	public final static double SIMULATION_LENGTH = 1340.0;

	public static void main(String[] args) {

		final int list = Schedule.addNewEventList(MessagingEventList.class);
		
		XYCollector temp = new XYCollector(list);
		XYCollector load = new XYCollector(list);
		VarianceGenerator vg = new VarianceGenerator(MAX_VARIANCE,
				MAX_VARIANCE_FRACTION);
		ArrayList<Fridge> fridges = new ArrayList<Fridge>(POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Fridge f = new Fridge(vg);
			f.setEventListID(list);
			f.addPropertyChangeListener(Fridge.PROP_TEMPERATURE, temp);
			f.addPropertyChangeListener(Fridge.PROP_LOAD, load);
			fridges.add(f);
		}

		final ProgressComposite pc = ProgressComposite.prepareOpening();
		final MessagingEventList el = (MessagingEventList) Schedule.getEventList(list);
		el.stopAtTime(SIMULATION_LENGTH);
		el.reset();
		el.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME,
				new ProgressListener(SIMULATION_LENGTH) {
					protected void progressChanged(int progress) {
						pc.setProgress(progress);
					}
				});

		Thread sim = new Thread() {
			public void run() {
				System.out.println("Starting simulation.");
				el.startSimulation();
				System.out.println("Stopping simulation.");
			}
		};
		sim.start();
		pc.open();
		System.out.println("Creating report.");
		TemperatureChart.openChart(temp.getResults());
		LoadChart.openChart(load.getResults());
	}
}