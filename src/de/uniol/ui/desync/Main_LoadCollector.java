package de.uniol.ui.desync;

import java.io.File;
import java.util.HashMap;

import simkit.Schedule;
import simkit.random.LKSeeds;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

public class Main_LoadCollector {

	private static int runs = 2;
	private static File file = new File(System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "out.csv");
	
	public static void main(String[] args) {
		HashMap<Configuration, TimeseriesMultiMeanCollector> results = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		// Perform experiment(s)
		for (int i = 1; i <= runs; i++) {
			Configuration conf = new Configuration();
			conf.variate_mc_seed = LKSeeds.ZRNG[i];
			conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			Experiment exp = new Experiment(conf, i, 0);
			exp.run(el, i == runs);
			
			// Store results
			results.put(conf, exp.getMeanLoad());
			
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
		// Output results
		ResultWriter.writeLoadResults(results, file);
	}
}