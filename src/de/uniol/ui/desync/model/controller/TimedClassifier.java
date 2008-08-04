package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.AbstractFridge;

public class TimedClassifier {

	public static enum classes {
		Z, A, B, B1, C, C1, D
	}
	
	protected AbstractFridge f;

	protected double tA = Double.NaN;
	protected double tB = Double.NaN;
	protected double tC = Double.NaN;
	protected double tC1 = Double.NaN;
	
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
		tC1 = tAct + tau_reduce - tauW;
		tB = tAct - tauC;
		double TMaxAct = sCC1(tAct);
		double ac = ((f.getT_min() - f.getT_max()) / f.tauCooling(f
				.getQ_cooling()));
		tA = tAct - ((TMaxAct - f.getT_max()) / ac);
		double Tt = f.getT_current();
		double sDC = sDC(t);
		double sCC1 = sCC1(t);
		double sCB = sCB(t);
		double sBA = sBA(t);
		// Classify fridge
		if (Tt > sBA) {
			// Class A: Fridge will not reach T_max_act
			return classes.A;
		} else if (Tt > sCB && Tt <= sBA && Tt > sCC1) {
			// Class B: Fridge can reach T_max_act, but not T_min
			return classes.B;
		} else if (Tt <= sCC1 && Tt > sCB) {
			// Class B1: Fridge does not need to cooldown any more, but is able
			// to reach Tmin
			return classes.B1;
		} else if (Tt >= sDC && Tt > sCC1 && Tt <= sCB) {
			// Class C: Fridge can reach T_min
			return classes.C;
		} else if (Tt <= sCC1 && Tt <= sCB) {
			// Class C1: Fridge does not need to cooldown any more, but cannot
			// reach Tmin
			return classes.C1;
		} else if (Tt < sDC) {
			// Class E: too much time till tAct
			return classes.D;
		} else {
			// Class Z: Should not happen!!
			return classes.Z;
		}
	}
	
	protected double sDC(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_max()
				+ (((f.getT_min() - f.getT_max()) / f.tauCooling(f
						.getQ_cooling())) * (t - tC));
	}
	
	protected double sCC1(double t) {
		if (f == null || Double.isNaN(tC)) {
			return Double.NaN;
		}
		return f.getT_min()
				+ (((f.getT_max() - f.getT_min()) / f.tauWarming(f
						.getQ_warming())) * (t - tC1));
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

	public double getTA() {
		return tA;
	}

	public double getTB() {
		return tB;
	}

	public double getTC() {
		return tC;
	}

	public double getTC1() {
		return tC1;
	}
}