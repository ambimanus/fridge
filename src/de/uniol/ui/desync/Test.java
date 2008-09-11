package de.uniol.ui.desync;

import simkit.stat.SimpleStatsTally;

public class Test {

	public static void main(String[] args) {
		SimpleStatsTally sst = new SimpleStatsTally();
		sst.newObservation(0.074);
		sst.newObservation(0.077);
		sst.newObservation(0.087);
		sst.newObservation(0.085);
		sst.newObservation(0.078);
		
		System.out.println("\tn        = " + sst.getCount());
		System.out.println("\tmin      = " + sst.getMinObs());
		System.out.println("\tmax      = " + sst.getMaxObs());
		System.out.println("\tmean     = " + sst.getMean());
		System.out.println("\tvariance = " + sst.getVariance());
		System.out.println("\tstd.dev  = " + sst.getStandardDeviation());
	}
}
