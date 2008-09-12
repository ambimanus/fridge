package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import simkit.Schedule;
import simkit.random.LKSeeds;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

public class Main_MultipleRuns {

	private static int runs = 5;
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		SimpleStatsTally sst = new SimpleStatsTally();
		HashMap<Configuration, TimeseriesMultiMeanCollector> temps = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		HashMap<Configuration, TimeseriesMultiMeanCollector> loads = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		// Perform experiment(s)
		for (int i = 1; i <= runs; i++) {
			Configuration conf = new Configuration();
			conf.showResults = false;
			if (i < 99) {
				conf.variate_mc_seed = LKSeeds.ZRNG[i + 1];
				conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i - 1];
			} else {
				conf.variate_mc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_Tcurrent_seed = Math
						.round(Math.random() * 1000000000d);
			}

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			// Run
			Experiment exp = new Experiment(conf);
			exp.run(el, i == runs);
			
			// Print current results
			System.out.println(exp.getName() + "(" + conf.SIMULATION_LENGTH
					+ "h) - " + exp.getSimulationTime() + "s");
			if (exp.getLoadStats() != null) {
				SimpleStatsTimeVarying sstv = exp.getLoadStats();
				System.out.println("\nStats of current run:");
				System.out.println("\tn        = " + sstv.getCount());
				System.out.println("\tmin      = " + sstv.getMinObs());
				System.out.println("\tmax      = " + sstv.getMaxObs());
				System.out.println("\tmean     = " + sstv.getMean());
				System.out.println("\tvariance = " + sstv.getVariance());
				System.out.println("\tstd.dev  = " + sstv.getStandardDeviation());
				System.out.println();
				sst.newObservation(sstv.getMean());
			} else {
				System.out.println();
			}
			
			// Store current result values
			temps.put(conf, exp.getMeanTemp());
			loads.put(conf, exp.getMeanLoad());
			
//			double[][] res = exp.getMeanLoad().getResults();
//			System.out.println(Arrays.toString(res[0]).replaceAll(", ", "\n").replaceAll("\\.", ","));
//			System.out.println(Arrays.toString(res[1]).replaceAll(", ", "\n").replaceAll("\\.", ","));
			
			// Cleanup
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
			}
			el.reset();
			el.coldReset();
//			exp.clear();
//			SimEntityClean.coldReset();
			exp = null;
			System.gc();
		}
		
		// Print overall stats
		System.out.println("\nOverall stats:");
		System.out.println("\tn        = " + sst.getCount());
		System.out.println("\tmin      = " + sst.getMinObs());
		System.out.println("\tmax      = " + sst.getMaxObs());
		System.out.println("\tmean     = " + sst.getMean());
		System.out.println("\tvariance = " + sst.getVariance());
		System.out.println("\tstd.dev  = " + sst.getStandardDeviation());

		// Calculate overall results
		double[][] tData = ResultWriter.convertToMilliseconds(ResultWriter
				.mean(ResultWriter.interpolate(temps, true)));
		double[][] lData = ResultWriter.convertToMilliseconds(ResultWriter
				.mean(ResultWriter.interpolate(loads, false)));
//		System.out.println(Arrays.toString(lData[0]).replaceAll(", ", "\n").replaceAll("\\.", ","));
//		System.out.println(Arrays.toString(lData[1]).replaceAll(", ", "\n").replaceAll("\\.", ","));
		
		// Print status
		System.out.println("Experiments and analyzation finished in "
				+ ((System.currentTimeMillis() - start) / 1000.0) + "s.");
		
		// Show overall results
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		// Temperature chart
		LineChartDialog lcd = new LineChartDialog(shell,
				"Temperature progress", "Time (h)", "Temperature (°C)", "min",
				"°C", 3.0, 8.0);
		lcd.addSeries("temperature (mean of means)", tData);
		lcd.setSeriesColor(0, SystemColor.BLACK);
		lcd.create();
		// Load chart
		StepChartDialog scd = new StepChartDialog(shell, "Load progress",
				"Time (h)", "Load (W)", "min", "W", 0.0, 70.0);
		scd.addSeries("load (mean of means)", lData);
		scd.setSeriesColor(0, SystemColor.BLACK);
		scd.create();
		// Open shell
		shell.setSize(900, 800);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}