package de.uniol.ui.model;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.ProgressComposite;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ProgressListener;
import de.uniol.ui.desync.util.collectors.AbstractCollector;
import de.uniol.ui.desync.util.collectors.TimeseriesCollector;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

public class Experiment {

	/** The underlying FEL */
	private MessagingEventList el;
	/** The simulated entities */
	private ArrayList<Fridge> fridges;

	/* Statistics collectors */
	private static ArrayList<TimeseriesCollector> temps = new ArrayList<TimeseriesCollector>();
	private static ArrayList<TimeseriesCollector> loads = new ArrayList<TimeseriesCollector>();
	private TimeseriesMultiMeanCollector meanTemp;
	private TimeseriesMultiMeanCollector meanLoad;

	public Experiment(MessagingEventList el, ArrayList<Fridge> fridges) {
		this.el = el;
		this.fridges = fridges;
		initStatistics();
	}

	private void initStatistics() {
		meanTemp = new TimeseriesMultiMeanCollector(el, "mean temperature");
		meanLoad = new TimeseriesMultiMeanCollector(el, "mean load");
		for (Fridge f : fridges) {
			meanTemp.addEntity(f, Fridge.PROP_TEMPERATURE);
			meanLoad.addEntity(f, Fridge.PROP_LOAD);

			TimeseriesCollector t = new TimeseriesCollector(el,
					"Temperature of " + f.getName(), f, Fridge.PROP_TEMPERATURE);
			temps.add(t);

			TimeseriesCollector l = new TimeseriesCollector(el, "Load of "
					+ f.getName(), f, Fridge.PROP_LOAD);
			loads.add(l);
		}
	}

	public void simulate(double end) {
		final ProgressComposite pc = new ProgressComposite();
		el.stopAtTime(end);
		el.reset();
		el.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME,
				new ProgressListener(end) {
					protected void progressChanged(int progress) {
						pc.setProgress(progress);
					}
				});
		new Thread() {
			public void run() {
				el.startSimulation();
			}
		}.start();
		pc.open();
	}

	public void showResults(boolean showAll, boolean highlightFirst,
			Color firstColor) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setSize(900, 800);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		// Temperature chart
		LineChartDialog lcd = new LineChartDialog(shell,
				"Temperature progress", "Time (min)", "Temperature (°C)",
				"min", "°C");
		lcd.addSeries(meanTemp);
		if (firstColor != null) {
			lcd.setSeriesColor(0, firstColor);
		}
		if (highlightFirst) {
			lcd.setSeriesWidth(0, 2.0f);
		}
		if (showAll) {
			lcd.addAllSeries(temps);
		}
		lcd.create();
		// Add load observations at finish time
		if (meanLoad.getSize() != 0) {
			meanLoad.addObservation(el.getStopTime(), meanLoad
					.getObservation(meanLoad.getSize() - 1)[1]);
		}
		for (AbstractCollector col : loads) {
			if (col.getSize() != 0) {
				col.addObservation(el.getStopTime(), col.getObservation(col
						.getSize() - 1)[1]);
			}
		}
		// Load chart
		StepChartDialog scd = new StepChartDialog(shell, "Load progress",
				"Time (min)", "Load (W)", "min", "W", -10.0, 80.0);
		scd.addSeries(meanLoad);
		if (firstColor != null) {
			scd.setSeriesColor(0, firstColor);
		}
		if (highlightFirst) {
			scd.setSeriesWidth(0, 2.0f);
		}
		if (showAll) {
			scd.addAllSeries(loads);
		}
		scd.open(true);
	}
}