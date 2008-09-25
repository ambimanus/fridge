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
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;

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