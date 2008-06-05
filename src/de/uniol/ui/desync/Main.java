package de.uniol.ui.desync;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import simkit.Schedule;
import de.uniol.ui.desync.ui.ChartDialog;
import de.uniol.ui.desync.ui.ProgressComposite;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ProgressListener;
import de.uniol.ui.desync.util.VarianceGenerator;
import de.uniol.ui.desync.util.collectors.AbstractCollector;
import de.uniol.ui.desync.util.collectors.LinearCollector;
import de.uniol.ui.desync.util.collectors.RectangularCollector;
import de.uniol.ui.model.Fridge;

public class Main {

	public final static int POPULATION_SIZE = 30;
	public final static double MAX_VARIANCE = 0.2;
	public final static int MAX_VARIANCE_FRACTION = 2;
//	public final static double SIMULATION_LENGTH = 1340.0;
	public final static double SIMULATION_LENGTH = 800.0;
	
	private static ArrayList<LinearCollector> temps = new ArrayList<LinearCollector>();
	private static ArrayList<RectangularCollector> loads = new ArrayList<RectangularCollector>();
	
	public static void main(String[] args) {

		final int list = Schedule.addNewEventList(MessagingEventList.class);
		
		AbstractCollector temp = new LinearCollector(list, "mean temperature");
		AbstractCollector load = new RectangularCollector(list, "mean load");
		VarianceGenerator vg = new VarianceGenerator(MAX_VARIANCE,
				MAX_VARIANCE_FRACTION);
		ArrayList<Fridge> fridges = new ArrayList<Fridge>(POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Fridge f = new Fridge(vg);
			f.setEventListID(list);
			f.addPropertyChangeListener(Fridge.PROP_TEMPERATURE, temp);
			
			LinearCollector t = new LinearCollector(list, f.getName());
			f.addPropertyChangeListener(Fridge.PROP_TEMPERATURE, t);
			temps.add(t);
			
			RectangularCollector l = new RectangularCollector(list, f.getName());
			f.addPropertyChangeListener(Fridge.PROP_LOAD, l);
			loads.add(l);
			
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
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setSize(900, 800);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		ChartDialog cd = new ChartDialog(shell, "Temperature progress", "Time (min)",
				"Temperature (°C)");
		cd.addSeries(temp);
		cd.addAllSeries(temps);
		cd.create();
		ChartDialog cd2 = new ChartDialog(shell, "Load progress", "Time (min)",
		"Load (W)");
		cd2.addSeries(load);
		cd2.addAllSeries(loads);
		cd2.open(true);
//		TemperatureChart.openChart(temp.getResults(), false);
//		LoadChart.openChart(load.getResults(), true);
	}
}