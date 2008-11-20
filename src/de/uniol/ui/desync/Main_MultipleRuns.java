/*
 * AdaptiveFridge Copyright (C) 2008 Christian Hinrichs
 * 
 * AdaptiveFridge is copyright under the GNU General Public License.
 * 
 * This file is part of AdaptiveFridge.
 * 
 * AdaptiveFridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AdaptiveFridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AdaptiveFridge.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniol.ui.desync;

import java.awt.SystemColor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import simkit.Schedule;
import simkit.random.LKSeeds;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.Configuration;
import de.uniol.ui.desync.model.Experiment;
import de.uniol.ui.desync.model.Configuration.DAMPING;
import de.uniol.ui.desync.model.Configuration.MODEL;
import de.uniol.ui.desync.model.Configuration.SIGNAL;
import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

/**
 * This class was mainly used to produce the results used in the second part of
 * the thesis. It follows the rules of statistically correct data by rerunning the
 * experiments a sufficient number of times with different random numbers.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class Main_MultipleRuns {

	private static File file = new File(System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "out.csv");
	
	public static void main(String[] args) {
//		runAndShow();
		runAndWrite();
	}
	
	/**
	 * Runs objectives and shows the results afterwards.
	 */
	private static void runAndShow() {
		/* Create results list */
		HashMap<String, double[][][]> results = new HashMap<String, double[][][]>();
		ArrayList<String> sortedKeys = new ArrayList<String>();
		long start = System.currentTimeMillis();
		
		/* Perform runs: */
		Configuration conf;
		ArrayList<Configuration> objectives;
		
//		objectives = Objectives.createObjectives_T();
//		objectives = Objectives.createObjectives_mc();
//		objectives = Objectives.createObjectives_A();
//		objectives = Objectives.createObjectives_TO();
//		objectives = Objectives.createObjectives_eta();
		
//		objectives = Objectives.createObjectives_T_mc();
//		objectives = Objectives.createObjectives_AllVariated_TLR_Random();
//		objectives = Objectives.createObjectives_AllVariated_DifferentStrategies();
		
//		objectives = Objectives.createObjectives_Lamps();
//		objectives = Objectives.createObjectives_Spread();
//		objectives = Objectives.createObjectives_TLR_preload();
		objectives = Objectives.createObjectives_TLR_reduce();
//		objectives = Objectives.createObjectives_Default();
//		objectives = Objectives.createObjectives_Iterative();
		
		Iterator<Configuration> it = objectives.iterator();
		int i = 0;
		while (it.hasNext()) {
			conf = it.next();
			sortedKeys.add(conf.title);
			results.put(conf.title, run(conf, i++));
		}

		/* Print status */
		System.out.println("\n*********************************************");
		long dur = System.currentTimeMillis() - start;
		long min = dur / 60000l;
		long sec = (dur % 60000l) / 1000l;
		System.out.println("All experiments finished in " + min + "m" + sec
				+ "s");

		/* Show overall results */
		showResults(sortedKeys, results);
	}

	/**
	 * Runs an experiment and writes the results afterwards to a file.
	 */
	private static void runAndWrite() {
		long start = System.currentTimeMillis();
		
		Configuration conf = new Configuration();
		conf.model = MODEL.COMPACT_LINEAR;
		conf.repetitions = 5;
		conf.SIMULATION_LENGTH = 20.0;
		conf.POPULATION_SIZE = 5000;
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.TLR;
		conf.damping = DAMPING.RANDOM;
//		conf.direct_t_notify = 6.0 * 60.0;
//		conf.direct_spread = 10.0;
//		conf.direct_doUnload = true;
		conf.timed_t_notify = 6.0 * 60.0;
		conf.timed_tau_preload = 10.0;
		conf.timed_tau_reduce = 120.0;
		conf.title = "event simulation";
		double[][][] result = run(conf, 0);
		
		// Write first result to file
		ResultWriter.writeResultsSimple(result[0], result[1], file);

		/* Print status */
		System.out.println("\n*********************************************");
		long dur = System.currentTimeMillis() - start;
		long min = dur / 60000l;
		long sec = (dur % 60000l) / 1000l;
		System.out.println("All experiments finished in " + min + "m" + sec
				+ "s");
	}

	/**
	 * Runs an experiment with the given configuration. The
	 * <code>instance</code> parameter defines the instance counter (for window
	 * titles etc).
	 * 
	 * @param conf
	 * @param instance
	 * @return the results as double[][][] = { temperature[][], load[][] }
	 */
	protected static double[][][] run(Configuration conf, int instance) {
		long start = System.currentTimeMillis();
		SimpleStatsTally sst = new SimpleStatsTally();
		HashMap<Configuration, TimeseriesMultiMeanCollector> temps = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		HashMap<Configuration, TimeseriesMultiMeanCollector> loads = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		// Perform experiment(s)
		for (int i = 1; i <= conf.repetitions; i++) {
			Configuration.distinct++;
			conf.showResults = false;
			if (i < 99) {
				conf.variate_Tcurrent_seed = getSeed(i);
				conf.variate_mc_seed = getSeed(i + 1);
				conf.variate_A_seed = getSeed(i + 2);
				conf.variate_TO_seed = getSeed(i + 3);
				conf.variate_eta_seed = getSeed(i + 4);
				conf.variate_qc_seed = getSeed(i + 5);
				conf.variate_qw_seed = getSeed(i + 6);
			} else {
				conf.variate_Tcurrent_seed = Math
						.round(Math.random() * 1000000000d);
				conf.variate_mc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_A_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_TO_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_eta_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_qc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_qw_seed = Math.round(Math.random() * 1000000000d);
			}

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			// Run
			Experiment exp = new Experiment(conf, instance, i - 1);
			exp.run(el, i == conf.repetitions);
			
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
			
//			double[][] res = exp.getMeanTemp().getResults();
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
		long dur = System.currentTimeMillis() - start;
		long min = dur / 60000l;
		long sec = (dur % 60000l) / 1000l;
		System.out.println("\n" + conf.title + " finished in " + min + "m"
				+ sec + "s");
		
		// Return results
		return new double[][][] { tData, lData };
	}
	
	/**
	 * @param index
	 * @return the seed in {@link LKSeeds#ZRNG} with given index
	 */
	protected static long getSeed(int index) {
		if (index > 99) {
			index -= 99;
		}
		return LKSeeds.ZRNG[index];
	}

	/**
	 * Opens the graphical results viewer with the performed experiment results.
	 * 
	 * @param keys
	 *            list of configurations of the performed experiments
	 * @param results
	 *            hashmap which contains the results arrays, mapped to their
	 *            configurations
	 */
	protected static void showResults(ArrayList<String> keys,
			HashMap<String, double[][][]> results) {
		// Prepare window
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		// Prepare temperature chart
		LineChartDialog lcd = new LineChartDialog(shell,
				"Temperature progress", "Time (h)", "Temperature (�C)", "min",
				"�C", 3.0, 8.0);
		// Prepare load chart
		StepChartDialog scd = new StepChartDialog(shell, "Load progress",
				"Time (h)", "Load (W)", "min", "W", 0.0, 70.0);
		// Create series
		for (String key : keys) {
			double[][][] data = results.get(key);
			double[][] tData = data[0];
			double[][] lData = data[1];
			lcd.addSeries(key, tData);
			if (keys.size() == 1) {
				lcd.setSeriesColor(1, SystemColor.BLACK);
			}
			scd.addSeries(key, lData);
			if (keys.size() == 1) {
				scd.setSeriesColor(1, SystemColor.BLACK);
			}
		}
		// Finish charts
		lcd.create();
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