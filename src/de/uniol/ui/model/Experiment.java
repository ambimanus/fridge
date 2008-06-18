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
	private ArrayList<? extends AbstractFridge> fridges;

	/* Statistics collectors */
	private static ArrayList<TimeseriesCollector> temps = new ArrayList<TimeseriesCollector>();
	private static ArrayList<TimeseriesCollector> loads = new ArrayList<TimeseriesCollector>();
	private TimeseriesMultiMeanCollector meanTemp;
	private TimeseriesMultiMeanCollector meanLoad;
	private boolean collectTemperature = false;
	private boolean collectLoad = true;

	public Experiment(MessagingEventList el, ArrayList<? extends AbstractFridge> fridges) {
		this.el = el;
		this.fridges = fridges;
		initStatistics();
	}

	private void initStatistics() {
		if (collectTemperature) {
			meanTemp = new TimeseriesMultiMeanCollector(el, "mean temperature");
		}
		if (collectLoad) {
			meanLoad = new TimeseriesMultiMeanCollector(el, "mean load");
		}
		for (AbstractFridge f : fridges) {
			if (collectTemperature) {
				meanTemp.addEntity(f, AbstractFridge.PROP_TEMPERATURE);
				TimeseriesCollector t = new TimeseriesCollector(el,
						"Temperature of " + f.getName(), f,
						Fridge.PROP_TEMPERATURE);
				temps.add(t);
			}
			if (collectLoad) {
				meanLoad.addEntity(f, AbstractFridge.PROP_LOAD);
				TimeseriesCollector l = new TimeseriesCollector(el, "Load of "
						+ f.getName(), f, AbstractFridge.PROP_LOAD);
				loads.add(l);
			}
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
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		int numberOfCharts = 0;
		if (collectTemperature) {
			// Temperature chart
			LineChartDialog lcd = new LineChartDialog(shell,
					"Temperature progress", "Time (min)", "Temperature (°C)",
					"min", "°C", 2.0, 9.0);
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
			numberOfCharts++;
		}
		if (collectLoad) {
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
			scd.create();
			numberOfCharts++;
		}
		
		// Open shell
		shell.setSize(900, numberOfCharts * 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public boolean isCollectTemperature() {
		return collectTemperature;
	}

	public void setCollectTemperature(boolean collectTemperature) {
		this.collectTemperature = collectTemperature;
	}

	public boolean isCollectLoad() {
		return collectLoad;
	}

	public void setCollectLoad(boolean collectLoad) {
		this.collectLoad = collectLoad;
	}
}