package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import simkit.Schedule;
import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.ProgressComposite;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ProgressListener;
import de.uniol.ui.desync.util.VarianceGenerator;
import de.uniol.ui.desync.util.collectors.MultiLinearCollector;
import de.uniol.ui.desync.util.collectors.XYCollector;
import de.uniol.ui.model.Fridge;

public class Main {

	public final static int POPULATION_SIZE = 5000;
	public final static double MAX_VARIANCE = 0.2;
	public final static int MAX_VARIANCE_FRACTION = 2;
	public final static double SIMULATION_LENGTH = 800.0;
	
	private static ArrayList<XYCollector> temps = new ArrayList<XYCollector>();
	private static ArrayList<XYCollector> loads = new ArrayList<XYCollector>();
	
	public static void main(String[] args) {
		final int list = Schedule.addNewEventList(MessagingEventList.class);
		final ProgressComposite pc = ProgressComposite.prepareOpening();
		final MessagingEventList el = (MessagingEventList) Schedule.getEventList(list);
		
		MultiLinearCollector temp = new MultiLinearCollector(el, "mean temperature");
		MultiLinearCollector load = new MultiLinearCollector(el, "mean load");
		VarianceGenerator vg = new VarianceGenerator(MAX_VARIANCE,
				MAX_VARIANCE_FRACTION);
		ArrayList<Fridge> fridges = new ArrayList<Fridge>(POPULATION_SIZE);
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Fridge f = new Fridge(vg);
			f.setEventListID(list);
			fridges.add(f);
			
			temp.addEntity(f, Fridge.PROP_TEMPERATURE);
			load.addEntity(f, Fridge.PROP_LOAD);
			
//			XYCollector t = new XYCollector(list, f.getName());
//			f.addPropertyChangeListener(Fridge.PROP_TEMPERATURE, t);
//			temps.add(t);
//			
//			XYCollector l = new XYCollector(list, f.getName());
//			f.addPropertyChangeListener(Fridge.PROP_LOAD, l);
//			loads.add(l);
		}
		

		el.stopAtTime(SIMULATION_LENGTH);
		el.reset();
		el.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME,
				new ProgressListener(SIMULATION_LENGTH) {
					protected void progressChanged(int progress) {
						pc.setProgress(progress);
					}
				});
		new Thread() {
			public void run() {
				System.out.println("Starting simulation.");
				el.startSimulation();
				System.out.println("Stopping simulation.");
			}
		}.start();
		pc.open();
		
		System.out.println("Creating report.");
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setSize(900, 800);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		LineChartDialog lcd = new LineChartDialog(shell, "Temperature progress", "Time (min)",
				"Temperature (°C)");
		lcd.addSeries(temp);
		lcd.addAllSeries(temps);
		lcd.setSeriesColor(0, SystemColor.BLACK);
//		lcd.setSeriesWidth(0, 2.0f);
		lcd.create();
		StepChartDialog scd = new StepChartDialog(shell, "Load progress", "Time (min)",
		"Load (W)");
		scd.addSeries(load);
		scd.addAllSeries(loads);
		scd.setSeriesColor(0, SystemColor.BLACK);
//		scd.setSeriesWidth(0, 2.0f);
		scd.open(true);
	}
}