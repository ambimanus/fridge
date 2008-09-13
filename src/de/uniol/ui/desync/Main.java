package de.uniol.ui.desync;

import simkit.Schedule;
import simkit.random.LKSeeds;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.util.MessagingEventList;

public class Main {

	private static int runs = 5;
	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		SimpleStatsTally sst = new SimpleStatsTally();
		// Perform experiment(s)
		for (int i = 1; i <= runs; i++) {
			if (i < 99) {
				conf.variate_mc_seed = LKSeeds.ZRNG[i];
				conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];
			} else {
				conf.variate_mc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_Tcurrent_seed = Math
						.round(Math.random() * 1000000000d);
			}

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			Experiment exp = new Experiment(conf, i, 0);
			exp.run(el, i == runs);
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
		System.out.println("\nOverall stats:");
		System.out.println("\tn        = " + sst.getCount());
		System.out.println("\tmin      = " + sst.getMinObs());
		System.out.println("\tmax      = " + sst.getMaxObs());
		System.out.println("\tmean     = " + sst.getMean());
		System.out.println("\tvariance = " + sst.getVariance());
		System.out.println("\tstd.dev  = " + sst.getStandardDeviation());
	}
}