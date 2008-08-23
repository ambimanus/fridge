package de.uniol.ui.desync.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import de.uniol.ui.desync.Configuration;
import de.uniol.ui.desync.Experiment;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

public class ResultWriter {

	protected static int counter = 0;
	protected static NumberFormat tf = NumberFormat.getNumberInstance();
	protected static NumberFormat vf = NumberFormat.getNumberInstance();
	protected static NumberFormat cf = NumberFormat.getNumberInstance();
	static {
		tf.setGroupingUsed(false);
		tf.setMinimumFractionDigits(5);
		tf.setMaximumFractionDigits(5);
		tf.setMinimumIntegerDigits(3);
		tf.setMaximumIntegerDigits(3);
		
		vf.setGroupingUsed(false);
		vf.setMinimumFractionDigits(2);
		vf.setMaximumFractionDigits(2);
		vf.setMinimumIntegerDigits(2);
		vf.setMaximumIntegerDigits(2);
		
		cf.setGroupingUsed(false);
		cf.setMinimumFractionDigits(1);
	}

	public static void writeResults(Configuration config, Experiment exp,
			File file) {
		double[][] t = exp.getTemperatureResults();
		double[][] l = exp.getLoadResults();
		if (t == null || l == null) {
			throw new IllegalArgumentException();
		}
		boolean append = file.exists();
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, append);
			if (!append) {
				// Write file header
				fw.write("Fridge Simulation result file, created "
						+ DateFormat.getInstance().format(new Date()));
				counter = 0;
			}
			// Write experiment header
			fw.write("\n\n******************  Experiment no." + counter
					+ "  ******************");
			// Write configuration
			writeConfig(fw, config);
			fw.write("\n\n*** Results:");
			// Write values
			fw.write("\n\ntime\ttemperature\t\ttime\tload");
			for (int i = 0; i < Math.max(t[0].length, l[0].length); i++) {
				// Divide time value by 60000d to get minutes
				if (i < t[0].length) {
					fw.write('\n' + tf.format(t[0][i] / 60000d) + '\t'
							+ vf.format(t[1][i]));
				}
				if (i < l[0].length) {
					if (i >= t[0].length) {
						fw.write("\n\t");
					}
					fw.write("\t\t" + tf.format(l[0][i] / 60000d) + '\t'
							+ vf.format(l[1][i]));
				}
			}
			counter++;
			// Close stream
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static void writeConfig(FileWriter fw, Configuration config)
			throws IOException {
		fw.write("\n\n*** Configuration:\n");
		for (Field f : Configuration.class.getFields()) {
			try {
				fw.write('\n' + f.getName() + "\t\t" + f.get(config));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void writeLoadResults(
			HashMap<Configuration, TimeseriesMultiMeanCollector> results,
			File file) {
		// Interpolate data
		HashMap<Configuration, ArrayList<Double>[]> data = interpolate(results);
		// Write data
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, false);
			// Write file header
			fw.write("Fridge Simulation result file, created "
					+ DateFormat.getInstance().format(new Date()));
			// Write configs
			writeConfigs(fw, data.keySet());
			// Prepare values
			ArrayList<Double>[] main = null;
			ArrayList<ArrayList<Double>[]> lists = new ArrayList<ArrayList<Double>[]>();
			Iterator<Configuration> it = data.keySet().iterator();
			while (it.hasNext()) {
				ArrayList<Double>[] current = data.get(it.next());
				if (main == null) {
					main = current;
				}
				lists.add(current);
			}
			// Write values
			fw.write("\n\n*** Results:");
			fw.write("\n\ntime");
			for (int i = 0; i < data.size(); i++) {
				fw.write("\tload");
			}
			for (int i = 0; i < main[0].size(); i++) {
				fw.write("\n" + tf.format(main[0].get(i)));
				for (ArrayList<Double>[] current : lists) {
					fw.write("\t" + vf.format(current[1].get(i)));
				}
			}
			// Close stream
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static void writeConfigs(FileWriter fw, Set<Configuration> configs)
			throws IOException {
		fw.write("\n\n*** Configuration:\n");
		for (Field f : Configuration.class.getFields()) {
			try {
				fw.write('\n' + f.getName());
				Iterator<Configuration> it = configs.iterator();
				while (it.hasNext()) {
					Object obj = f.get(it.next());
					if (obj instanceof Double) {
						fw.write("\t" + cf.format((Double) obj));
					} else {
						fw.write("\t" + obj);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static HashMap<Configuration, ArrayList<Double>[]> interpolate(
			HashMap<Configuration, TimeseriesMultiMeanCollector> data) {
		Set<Configuration> keys = data.keySet();
		HashMap<Configuration, ArrayList<Double>[]> ret = new HashMap<Configuration, ArrayList<Double>[]>(
				keys.size());
		// Create list of TimeSeriesIterators
		ArrayList<TimeSeriesIterator> its = new ArrayList<TimeSeriesIterator>(keys.size());
		Iterator<Configuration> it = keys.iterator();
		while (it.hasNext()) {
			Configuration key = it.next();
			TimeseriesMultiMeanCollector t = data.get(key);
			TimeSeriesIterator tsi = new TimeSeriesIterator(key, t.getTimes(),
					t.getValues());
			// Init
			tsi.next();
			// Add to list
			its.add(tsi);
		}
		// Walk through list, find lowest timestamp of all TSIs on each
		// iteration, and add the according value of the TSI to all result
		// lists.
		int size = data.get(keys.iterator().next()).getSize();
		ArrayList<TimeSeriesIterator> iteratable = new ArrayList<TimeSeriesIterator>(its);
		while (!iteratable.isEmpty()) {
			// Init pointer
			TimeSeriesIterator lowest = null;
			// Walk through TSIs and find lowest
			for (TimeSeriesIterator tsi : iteratable) {
				if (lowest == null
						|| tsi.getCurrentTime() < lowest.getCurrentTime()) {
					lowest = tsi;
				}
			}
			// Add found time/value to all result lists
			for (TimeSeriesIterator tsi : its) {
				ArrayList<Double>[] list = ret.get(tsi.getKey());
				if (list == null) {
					list = new ArrayList[2];
					list[0] = new ArrayList<Double>(size);
					list[1] = new ArrayList<Double>(size);
					ret.put(tsi.getKey(), list);
				}
				list[0].add(lowest.getCurrentTime());
				list[1].add(tsi.getCurrentValue());
			}
			// Move tsi to next time/value (or remove it if no more values)
			if (!lowest.next()) {
				iteratable.remove(lowest);
			}
		}
		return ret;
	}
	
	protected static class TimeSeriesIterator {
		
		private Configuration key;
		private ArrayList<Double> times;
		private ArrayList<Double> values;
		private int tPointer = -1;
		private int vPointer = -1;
		private double currentTime;
		private double currentValue;
		
		public TimeSeriesIterator(Configuration key, ArrayList<Double> times,
				ArrayList<Double> values) {
			this.key = key;
			this.times = times;
			this.values = values;
		}
		
		public boolean next() {
			if (tPointer < times.size() - 1) {
				currentTime = times.get(++tPointer);
				currentValue = values.get(++vPointer);
				return true;
			} else {
				return false;
			}
		}

		/**
		 * @return the key
		 */
		public Configuration getKey() {
			return key;
		}

		/**
		 * @return the currentTime
		 */
		public double getCurrentTime() {
			return currentTime;
		}

		/**
		 * @return the currentValue
		 */
		public double getCurrentValue() {
			return currentValue;
		}
	}
}