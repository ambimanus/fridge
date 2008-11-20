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
import java.util.ArrayList;
import java.util.HashMap;

import simkit.Schedule;
import simkit.stat.SimpleStatsTally;
import de.uniol.ui.desync.model.Configuration;
import de.uniol.ui.desync.model.Experiment;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

/**
 * This class was used to produce a csv file with simple load values (one per
 * line) from an experiment.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class Main_LoadCollector {

	private static int runs = 1;
	private static File file = new File(System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "out.csv");
	
	public static void main(String[] args) {
		HashMap<Configuration, TimeseriesMultiMeanCollector> results = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		// Perform experiment(s)
		for (int i = 1; i <= runs; i++) {
			Configuration conf = new Configuration();
//			conf.variate_mc_seed = LKSeeds.ZRNG[i];
//			conf.variate_Tcurrent_seed = LKSeeds.ZRNG[LKSeeds.ZRNG.length - i];

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
		ArrayList<SimpleStatsTally> stats = ResultWriter.writeLoadResultsSimple(results, file);
		for (SimpleStatsTally sst : stats) {
			System.out.println("\nStats:");
			System.out.println("\tn        = " + sst.getCount());
			System.out.println("\tmin      = " + sst.getMinObs());
			System.out.println("\tmax      = " + sst.getMaxObs());
			System.out.println("\tmean     = " + sst.getMean());
			System.out.println("\tvariance = " + sst.getVariance());
			System.out.println("\tstd.dev  = " + sst.getStandardDeviation());
		}
	}
}