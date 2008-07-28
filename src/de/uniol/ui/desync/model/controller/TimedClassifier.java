package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.AbstractFridge;

public class TimedClassifier {

	public static enum classes {
		BLACK, RED, ORANGE, GREEN, BLUE, BROWN
	}
	
	protected AbstractFridge f;
	
	protected double tC = Double.NaN;
	protected double tD = Double.NaN;
	protected double tB = Double.NaN;
	protected double tA = Double.NaN;
	
	public TimedClassifier(AbstractFridge f) {
		this.f = f;
	}
	
	public classes classifyFridge(Double t, Double tau_preload,
			Double tau_reduce) {
		// Calculate working variables
		double tAct = t + tau_preload;
		double tauC = f.tauCooling(f.getQ_cooling());
		double tauW = f.tauWarming(f.getQ_warming());
		tC = tAct + tau_reduce - tauW - tauC;
		tD = tAct + tau_reduce - tauW;
		tB = tAct - tauC;
		double TMaxAct = sCD(tAct);
		double ac = ((f.getT_min() - f.getT_max()) / f.tauCooling(f
				.getQ_cooling()));
		tA = tAct - ((TMaxAct - f.getT_max()) / ac);
		double Tt = f.getT_current();
		double sEC = sEC(t);
		double sCD = sCD(t);
		double sCB = sCB(t);
		double sBA = sBA(t);
		// Classify fridge
		if (Tt < sEC) {
			// Class brown: too much time till tAct
			return classes.BROWN;
		} else if (Tt >= sEC && Tt > sCD && Tt <= sCB) {
			// Class green: Fridge can reach T_min
			return classes.GREEN;
		} else if (Tt <= sCD) {
			// Class blue: Fridge does not need to cooldown any more
			return classes.BLUE;
		} else if (Tt > sCB && Tt <= sBA) {
			// Class orange: Fridge can reach T_max_act, but not T_min
			return classes.ORANGE;
		} else if (Tt > sBA) {
			// Class red: Fridge will not reach T_max_act
			return classes.RED;
		} else {
			// Class black: Should not happen!!
			return classes.BLACK;
		}
	}
	
	protected double sEC(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_max()
				+ (((f.getT_min() - f.getT_max()) / f.tauCooling(f
						.getQ_cooling())) * (t - tC));
	}
	
	protected double sCD(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_min()
				+ (((f.getT_max() - f.getT_min()) / f.tauWarming(f
						.getQ_warming())) * (t - tD));
	}
	
	protected double sCB(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_max()
				+ (((f.getT_min() - f.getT_max()) / f.tauCooling(f
						.getQ_cooling())) * (t - tB));
	}
	
	protected double sBA(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_max()
				+ (((f.getT_min() - f.getT_max()) / f.tauCooling(f
						.getQ_cooling())) * (t - tA));
	}

	public double getTC() {
		return tC;
	}

	public double getTD() {
		return tD;
	}

	public double getTB() {
		return tB;
	}

	public double getTA() {
		return tA;
	}
}