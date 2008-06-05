package de.uniol.ui.desync.util;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateBase;
import simkit.random.RandomVariateFactory;

public class VarianceGenerator extends RandomVariateBase {

	public final static long DEFAULT_SEED = 4357L;
	
	/** Underlying integer random generator */
	private RandomVariate rv = null;
	/** Interval of resulting numbers: [1-variance,1+variance] */
	private double variance = 0.2;
	/** "Resolution": amount of fractional digits of resulting numbers */
	private int fraction = 2;
	private long seed;

	public VarianceGenerator(double variance, int fraction) {
		this(DEFAULT_SEED, variance, fraction);
	}
	
	public VarianceGenerator(long seed, double variance, int fraction) {
		this.variance = variance;
		this.fraction = fraction;
		this.seed = seed;
	}

	public double generate() {
		// Init uniform random generator with generated numbers in
		// [0, variance*2*(10^fraction)]
		if (rv == null) {
			rv = RandomVariateFactory.getInstance("DiscreteUniformVariate",
					RandomNumberFactory.getInstance("MersenneTwister", seed),
					new Object[] { 0,
							(int) (variance * 2.0 * Math.pow(10, fraction)) });
		}
		// Generate next random in [0, variance*2*(10^fraction)]
		double rnd = rv.generate();
		// Normalize to [0, variance*2]
		rnd /= Math.pow(10, fraction);
		// Normalize to [-variance,variance]
		rnd -= variance;
		// Normalize to [1-variance,1+variance]
		rnd += 1;
		return rnd;
	}

	public Object[] getParameters() {
		return new Object[] { variance };
	}

	public void setParameters(Object... params) {
		if (params.length != 1) {
			throw new IllegalArgumentException("Expected 1 parameter, but got "
					+ params.length);
		}
		if (!(params[0] instanceof Number)) {
			throw new IllegalArgumentException(
					"Expected numeric parameter, but got a "
							+ params[0].getClass());
		}
		variance = (Double) params[0];
	}
}