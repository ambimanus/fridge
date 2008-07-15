package de.uniol.ui.desync;

import simkit.Schedule;
import simkit.random.LKSeeds;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.util.MessagingEventList;

public class Main {

	private static int runs = 1;
	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		SimpleStatsTally sst = new SimpleStatsTally();
		// Get FEL
		final int list = Schedule.addNewEventList(MessagingEventList.class);
		final MessagingEventList el = (MessagingEventList) Schedule
				.getEventList(list);
		Experiment exp = new Experiment(conf);
		for (int i = 1; i <= runs; i++) {
			conf.variate_mc_seed = LKSeeds.ZRNG[i];
			conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];
			
			exp.run(el);
			
			double mean = exp.getLoadStats().getMean();
			System.out.println(exp.getName() + " - " + exp.getSimulationTime()
					+ "s - mean load = " + mean);
			sst.newObservation(mean);
			
			// Cleanup
			el.reset();
			el.coldReset();
			exp.clear();
			SimEntityClean.coldReset();
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