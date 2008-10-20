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

import simkit.random.Congruential;
import simkit.random.NormalVariate;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.desync.model.Configuration;
import de.uniol.ui.desync.model.Experiment;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

/**
 * Utility class which performs calcualtions on simulation results and is able
 * to format and write them to files.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class ResultWriter {

	/** counts the written results */
	protected static int counter = 0;
	/* some formatting */
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
		vf.setMinimumFractionDigits(3);
		vf.setMaximumFractionDigits(3);
		vf.setMinimumIntegerDigits(2);
		vf.setMaximumIntegerDigits(2);
		
		cf.setGroupingUsed(false);
		cf.setMinimumFractionDigits(1);
	}

	/**
	 * Writes the given values separated by the specified separator to the named
	 * file.
	 * 
	 * @param file
	 * @param data
	 * @param nf
	 * @param separator
	 */
	public static void writeArray(File file, double[] data, NumberFormat nf,
			String separator) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, false);
			// Write values
			for (int i = 0; i < data.length; i++) {
				fw.write(nf.format(data[i]));
				if (i < data.length - 1l) {
					fw.write(separator);
				}
			}
			// Close stream
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Produces a synthetic normal distribution and writes it to the named file.
	 * 
	 * @param file
	 * @param length
	 * @param seed
	 * @param mean
	 * @param sdev
	 */
	public static void writeNormalDistribution(File file, long length,
			long seed, double mean, double sdev) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, false);
			// Prepare values
			NormalVariate nv = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(seed);
			nv.setRandomNumber(cong);
			nv.setParameters(mean, sdev);
			// Write values
			for (long i = 0l; i < length; i++) {
				fw.write(vf.format(nv.generate()));
				if (i < length - 1l) {
					fw.write("\n");
				}
			}
			// Close stream
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the results of the given experiment to the named file. Adds a
	 * header and the used configuration to the top of the file.
	 * 
	 * @param config
	 * @param exp
	 * @param file
	 */
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
	
	/**
	 * Writes the given configuration to the named file.
	 * 
	 * @param fw
	 * @param config
	 * @throws IOException
	 */
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

	/**
	 * Writes the load result values denoted by the given hashmap to the named
	 * file. The hashmap may contain a number of results with different lenghts.
	 * An interpolation will be done to be able to directly compare the written
	 * values with each other. The configurations used to produce the results
	 * will be written at top of each 'column'.
	 * 
	 * @param results
	 * @param file
	 */
	public static void writeLoadResults(
			HashMap<Configuration, TimeseriesMultiMeanCollector> results,
			File file) {
		// Interpolate data
		HashMap<Configuration, ArrayList<Double>[]> data = interpolate(results,
				false);
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

	/**
	 * This method writes the load result values denoted by the given hashmap to
	 * the named file. The hashmap may contain a number of results with
	 * different lenghts. An interpolation will be done to be able to directly
	 * compare the written values with each other. For each value column, a
	 * {@link SimpleStatsTally} will be filled and added to the later returned
	 * list.
	 * 
	 * @param results
	 * @param file
	 * @return
	 */
	public static ArrayList<SimpleStatsTally> writeLoadResultsSimple(
			HashMap<Configuration, TimeseriesMultiMeanCollector> results,
			File file) {
		ArrayList<SimpleStatsTally> stats = new ArrayList<SimpleStatsTally>();
		// Interpolate data
		HashMap<Configuration, ArrayList<Double>[]> data = interpolate(results,
				false);
		// Write data
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, false);
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
			for (int i = 0; i < main[1].size(); i++) {
				for (int j = 0; j < lists.size(); j++) {
					ArrayList<Double>[] current = lists.get(j);
					fw.write(vf.format(current[1].get(i)));
					if (j >= stats.size()) {
						stats.add(new SimpleStatsTally());
					}
					SimpleStatsTally sst = stats.get(j);
					sst.newObservation(current[1].get(i));
					if (i < main[1].size() - 1) {
						fw.write("\n");
					}
				}
			}
			// Close stream
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stats;
	}

	/**
	 * Writes the given configurations to the specified file writer. The values
	 * will be written in columns, and each colum is separated by tabulators.
	 * 
	 * @param fw
	 * @param configs
	 * @throws IOException
	 */
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

	/**
	 * This method interpolates the given data by identifying the maximum data
	 * length and filling up the other series. The parameter
	 * <code>interpolateLinear</code> defines whether simple duplicates of the
	 * last value (false, used for load values), or a linear interpolation
	 * between the previous time/value point and the next will be done (true,
	 * used for temperature values).
	 * 
	 * @param data
	 * @param interpolateLinear
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<Configuration, ArrayList<Double>[]> interpolate(
			HashMap<Configuration, TimeseriesMultiMeanCollector> data,
			boolean interpolateLinear) {
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
				
				// Only proceed if this datapoint has not been processed
				if (list[0].size() == 0
						|| (list[0].get(list[0].size() - 1) != lowest
								.getCurrentTime())) {
					list[0].add(lowest.getCurrentTime());
					if (tsi == lowest
							|| tsi.getCurrentTime() == lowest.getCurrentTime()) {
						// Just add the current value
						list[1].add(tsi.getCurrentValue());
					} else {
						if (interpolateLinear) {
							// Take prev value and next value with according
							// times, and calculate current value by linear
							// interpolation
							boolean switchedTsiToPrevious = tsi.prev();
							double[] prev = new double[] {
									tsi.getCurrentTime(), tsi.getCurrentValue() };
							if (switchedTsiToPrevious) {
								tsi.next();
							}
							double[] next = new double[] {
									tsi.getCurrentTime(), tsi.getCurrentValue() };
							// equation of line through points prev and next:
							// y = y1 + (((y2-y1)/(x2-x1)) * (x-x1))
							double interpolatedValue = prev[1]
									+ (((next[1] - prev[1]) / (next[0] - prev[0])) * (lowest
											.getCurrentTime() - prev[0]));
							// Error checking
							if (Double.isNaN(interpolatedValue)) {
								// Division through zero or something like that,
								// just take prev value instead.
								interpolatedValue = prev[1];
							}
							list[1].add(interpolatedValue);
						} else {
							// Add previous value
							boolean switchedTsiToPrevious = tsi.prev();
							list[1].add(tsi.getCurrentValue());
							if (switchedTsiToPrevious) {
								tsi.next();
							}
						}
					}
				}
			}
			// Move tsi to next time/value (or remove it if no more values)
			if (!lowest.next()) {
				iteratable.remove(lowest);
			}
		}
		return ret;
	}

	/**
	 * This method gets a number of time series as input, each mapped to its
	 * configuration which was used to produce the results, and calculates the
	 * mean of all given time series for all points in time. In other words,
	 * this method merges the given input data to a single time series of mean
	 * values.<br>
	 * <b>Warning: the input data must be of the same length each.</b>
	 * 
	 * @param data
	 * @return
	 */
	public static double[][] mean(HashMap<Configuration, ArrayList<Double>[]> data) {
		ArrayList<Double>[] tester = data.values().iterator().next();
		int size = tester[0].size();
		double[][] ret = new double[2][size];
		for (int i = 0; i < size; i++) {
			SimpleStatsTally sstt = new SimpleStatsTally();
			SimpleStatsTally sstl = new SimpleStatsTally();
			Iterator<Configuration> it = data.keySet().iterator();
			while (it.hasNext()) {
				ArrayList<Double>[] current = data.get(it.next());
				sstt.newObservation(current[0].get(i));
				sstl.newObservation(current[1].get(i));
			}
			ret[0][i] = sstt.getMean();
			ret[1][i] = sstl.getMean();
		}
		return ret;
	}

	/**
	 * Converts the given time series' time values from minutes to milliseconds.
	 * 
	 * @param data
	 * @return
	 */
	public static double[][] convertToMilliseconds(double[][] data) {
		for (int i = 0; i < data[0].length; i++) {
			data[0][i] = data[0][i] * 60000d;
		}
		return data;
	}

	/**
	 * Helper class which is used in the interpolation of time series. It
	 * iterates over the values of a time series.
	 * 
	 * @author <a href=
	 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
	 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
	 * 
	 */
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
		
		public boolean prev() {
			if (tPointer > 0) {
				currentTime = times.get(--tPointer);
				currentValue = values.get(--vPointer);
				return true;
			} else {
				return false;
			}
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