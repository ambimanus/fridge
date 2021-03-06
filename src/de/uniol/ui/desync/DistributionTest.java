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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import simkit.Schedule;
import simkit.random.Congruential;
import simkit.random.LKSeeds;
import simkit.random.NormalVariate;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.Configuration;
import de.uniol.ui.desync.model.Experiment;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;

/**
 * This class first runs an experiment, and afterwards creates a synthetic
 * normal distribution based on mean and standard deviation of the load results
 * from the experiment. The experiment load results and the normal distributed
 * synthetic dataset are then each written to a separaten csv file.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class DistributionTest {

	private static File file1 = new File(System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "out.csv");
	private static File file2 = new File(System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "distribution.csv");
	
	public static void main(String[] args) {
		// Perform experiment
		Configuration conf = new Configuration();
//		conf.variate_mc_seed = LKSeeds.ZRNG[i];
//		conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];

		// Get FEL
		int list = Schedule.addNewEventList(MessagingEventList.class);
		MessagingEventList el = (MessagingEventList) Schedule
				.getEventList(list);
		
		Experiment exp = new Experiment(conf, 0, 0);
		exp.run(el, false);
		
		// Create results
		double[] values = exp.getMeanLoad().getResults()[1];
		SimpleStatsTally sst = new SimpleStatsTally();
		// Sort dataset
		Arrays.sort(values);
		// Remove duplicates
		ArrayList<Double> val = new ArrayList<Double>();
		for (int i = 0; i < values.length; i++) {
			// Round to 4 fraction digits
			double d = new BigDecimal(values[i]).setScale(4,
					RoundingMode.HALF_UP).doubleValue();
			if (i == 0 || val.get(val.size() - 1) != d) {
				val.add(d);
			}
		}
		// Recreate dataset with cleaned values
		values = new double[val.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = val.get(i);
			// Add to stats tally
			sst.newObservation(values[i]);
		}
		System.out.println("\nsst:");
		System.out.println("\tn        = " + sst.getCount());
		System.out.println("\tmin      = " + sst.getMinObs());
		System.out.println("\tmax      = " + sst.getMaxObs());
		System.out.println("\tmean     = " + sst.getMean());
		System.out.println("\tvariance = " + sst.getVariance());
		System.out.println("\tstd.dev  = " + sst.getStandardDeviation());
		SimpleStatsTimeVarying sstv = exp.getMeanLoad().getTimeVaryingStats();
		System.out.println("\nsstv:");
		System.out.println("\tn        = " + sstv.getCount());
		System.out.println("\tmin      = " + sstv.getMinObs());
		System.out.println("\tmax      = " + sstv.getMaxObs());
		System.out.println("\tmean     = " + sstv.getMean());
		System.out.println("\tvariance = " + sstv.getVariance());
		System.out.println("\tstd.dev  = " + sstv.getStandardDeviation());
		// Create normal distributed reference dataset
		double[] normal = new double[values.length];
		NormalVariate nv = new NormalVariate();
		Congruential cong = new Congruential();
		cong.setSeed(LKSeeds.ZRNG[50]);
		nv.setRandomNumber(cong);
		// Take params from collected stats tally
		nv.setParameters(sst.getMean(), sst.getStandardDeviation());
		for (int i = 0; i < normal.length; i++) {
			normal[i] = nv.generate();
		}
		// Sort reference dataset
		Arrays.sort(normal);
		
		// Output results
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(1);
		nf.setMaximumFractionDigits(4);
		ResultWriter.writeArray(file1, values, nf, "\n");
		ResultWriter.writeArray(file2, normal, nf, "\n");
//		ResultWriter.writeNormalDistribution(file2, loads.getTimeVaryingStats()
//				.getCount(), LKSeeds.ZRNG[50], loads.getTimeVaryingStats()
//				.getMean(), loads.getTimeVaryingStats().getStandardDeviation());
	}
}