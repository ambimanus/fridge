package de.uniol.ui.desync;

import simkit.Schedule;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.desync.util.MessagingEventList;

public class Main {

	private static int runs = 10;
//	private static File file = new File(System.getProperty("user.dir")
//			+ File.separator + "data" + File.separator + "out.csv");
	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		SimpleStatsTally sst = new SimpleStatsTally();
		// Perform experiment(s)
		for (int i = 1; i <= runs; i++) {
//			conf.variate_mc_seed = LKSeeds.ZRNG[i];
//			conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];
			conf.SIMULATION_LENGTH = 0.0 + i;

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			Experiment exp = new Experiment(conf);
			exp.run(el);
			System.out.print(exp.getName() + "(" + conf.SIMULATION_LENGTH
					+ "h) - " + exp.getSimulationTime() + "s");
			
			if (exp.getLoadStats() != null) {
				double mean = exp.getLoadStats().getMean();
				System.out.println(" - mean load = " + mean);
				sst.newObservation(mean);
			} else {
				System.out.println();
			}
			
			// Store results
//			ResultWriter.writeResults(conf, exp, file);
			
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