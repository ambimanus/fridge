package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.controller.TimedClassifier.classes;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.signals.Itlr;
import de.uniol.ui.desync.util.Geometry;

public class TimedControllerCompactLinear extends BaseControllerCompactLinear
		implements Itlr {
	
	protected final static String EV_DEL_AND_TARGET_TO = "DeleteAndTargetTo";
	
	public TimedControllerCompactLinear(LinearFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}

	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
//		strategyCoolImmediately(tau_preload, tau_reduce);
//		strategyCoolMin(tau_preload, tau_reduce);
//		strategyCoolFitting(tau_preload, tau_reduce);
		strategyCoolFitting2(tau_preload, tau_reduce);
	}
	
	protected void strategyCoolImmediately(Double tau_preload, Double tau_reduce) {
		// Classify f to apply proper cooling program
		double now = getEventList().getSimTime();
		TimedClassifier tc = new TimedClassifier(fridge);
		classes c = tc.classifyFridge(now, tau_preload, tau_reduce);
		switch (c) {
		case A:
		case B1:
		case B: {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
					.calculateTemperatureAfter(true, tau_preload, fridge
							.getT_current(), fridge.getQ_cooling()), fridge
					.getQ_cooling());
			break;
		}
		case C1:
		case C: {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge.getT_min(), fridge
					.getQ_cooling());
			break;
		}
		case D: {
			// Calculate intersection of current temperature curve with sDC
			int ret = intersectSDC(tau_preload, tau_reduce);
			if (ret == -1) {
				// No intersection found in given timespan. Reschedule this
				// again at a later point in time.
				double tAct = now + tau_preload;
				double tauC = fridge.tauCooling(fridge.getQ_cooling());
				double tauW = fridge.tauWarming(fridge.getQ_warming());
				double tC = tAct + tau_reduce - tauW - tauC;
				double delay = tC - now;
				waitDelay(EV_REDUCE_LOAD, delay, tau_preload - delay,
						tau_reduce);
			} else if (ret != 0) {
				// Should not happen
				System.err.println(getName()
						+ " - Error while intersection with sDC, ret=" + ret);
			}
			break;
		}
		default: {
			System.err.println(getName()
					+ ": unknown class, this should not happen.");
			break;
		}
		}
	}
	
	protected void strategyCoolMin(Double tau_preload, Double tau_reduce) {
		// TODO case "T_current > sCB(now)"
		// Calculate fitting cooling curve sCB
		int ret = intersectSCB(tau_preload, tau_reduce);
		if (ret != 0) {
			// No intersection found.
			System.err.println(getName()
					+ " - Error while intersection with sCB, ret=" + ret);
		}
	}
	
	protected void strategyCoolFitting(Double tau_preload, Double tau_reduce) {
		// Classify f to apply proper cooling program
		double now = getEventList().getSimTime();
		TimedClassifier tc = new TimedClassifier(fridge);
		classes c = tc.classifyFridge(now, tau_preload, tau_reduce);
		switch (c) {
		case A:
		case B1:
		case B: {
			waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
					.calculateTemperatureAfter(true, tau_preload, fridge
							.getT_current(), fridge.getQ_cooling()), fridge
					.getQ_cooling());
			break;
		}
		case C1:
		case C:
		case D: {
			// Calculate intersection of current temperature curve with sCB
			Line line = new Line();
			if (fridge.isActive()) {
				line.x1 = fridge.tau(fridge.getT_current(), fridge.getT_min(),
						fridge.getQ_cooling());
				line.y1 = fridge.getT_min();
				line.x2 = line.x1 + fridge.tauWarming(fridge.getQ_warming());
				line.y2 = fridge.getT_max();
			} else {
				line.x1 = 0.0;
				line.y1 = fridge.getT_current();
				line.x2 = line.x1
						+ fridge.tau(line.y1, fridge.getT_max(), fridge
								.getQ_warming());
				line.y2 = fridge.getT_max();
			}
			Line s = new Line();
			s.x1 = tc.getTB() - now;
			s.y1 = fridge.getT_max();
			s.x2 = s.x1 + fridge.tauCooling(fridge.getQ_cooling());
			s.y2 = fridge.getT_min();
			double[] is = new double[2];
			int ret = Geometry.findLineSegmentIntersection(line.x1, line.y1,
					line.x2, line.y2, s.x1, s.y1, s.x2, s.y2, is);
			if (ret >= 0) {
				if (is[0] > 0.0) {
					// If an intersection has occured (inside or outside the segment
					// bounds), schedule the classification again at that point in
					// time.
					waitDelay(EV_REDUCE_LOAD, is[0], tau_preload - is[0],
							tau_reduce);
				} else {
					// If the intersection is 'now', switch to cooling.
					waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
							.calculateTemperatureAfter(true, tau_preload,
									fridge.getT_current(), fridge
											.getQ_cooling()), fridge
							.getQ_cooling());
				}
			} else {
				// This should not happen.
				System.err.println(getName()
						+ " - class D: no intersection with sCB found!");
			}
			break;
		}
		default: {
			System.err.println(getName()
					+ ": unknown class, this should not happen.");
			break;
		}
		}
	}
	
	protected void strategyCoolFitting2(Double tau_preload, Double tau_reduce) {
		double now = getEventList().getSimTime();
		double tAct = now + tau_preload;
		double tauW = fridge.tauWarming(fridge.getQ_warming());
		// Check if we're able to survive tau_reduce and T_max-act is
		// in range [Tmin,Tmax]
		if (tau_reduce > tauW) {
			// We cannot survive tau_reduce, so T_max-act is < Tmin.
			// The best result will be got if we reach Tmin at t_act.
			int ret = intersectSCB(tau_preload, tau_reduce);
			if (ret != 0) {
				System.err.println(getName()
						+ " - Error while intersection with sCB, ret=" + ret);
			}
		} else {
			// We are able to survive tau_reduce. Try to reach T_max-act.
			// To do this, we have to check an intersecion with sCCa or sBA. We
			// don't know which one, so we have to test both.
			double tC1 = tAct + tau_reduce - tauW;
			double aw = (fridge.getT_max() - fridge.getT_min())
					/ fridge.tauWarming(fridge.getQ_warming());
			double T_maxact = fridge.getT_min() + (aw * (tAct - tC1));
			double ac = ((fridge.getT_min() - fridge.getT_max()) / fridge
					.tauCooling(fridge.getQ_cooling()));
			double tA = tAct - ((T_maxact - fridge.getT_max()) / ac);
			if (tC1 < tA) {
				// sCC1 starts first.
				// Find intersection with sCC1
				int ret = intersectSCC1(tau_preload, tau_reduce);
				if (ret == -1) {
					// There is no intersection with sCC1 in the timespan
					// till tAct. So we have to find an intersection with
					// sBA.
					ret = intersectSBA(tau_preload, tau_reduce);
					if (ret != 0) {
						System.err.println(getName()
								+ " - Error while intersection with sBA, ret="
								+ ret);
					}
				} else if (ret != 0) {
					System.err.println(getName()
							+ " - Error while intersection with sCC1, ret="
							+ ret);
				}
			} else {
				// sBA starts first.
				// Find intersection with sBA
				int ret = intersectSBA(tau_preload, tau_reduce);
				if (ret == -1) {
					// There is no intersection with sBA in the timespan
					// till tAct. So we have to find an intersection with
					// sCC1.
					ret = intersectSCC1(tau_preload, tau_reduce);
					if (ret != 0) {
						System.err.println(getName()
								+ " - Error while intersection with sCC1, ret="
								+ ret);
					}
				} else if (ret != 0) {
					System.err.println(getName()
							+ " - Error while intersection with sBA, ret="
							+ ret);
				}
			}
		}
	}
	
	protected int intersectSDC(double tau_preload, double tau_reduce) {
		double now = getEventList().getSimTime();
		double tAct = now + tau_preload;
		double tauC = fridge.tauCooling(fridge.getQ_cooling());
		double tauW = fridge.tauWarming(fridge.getQ_warming());
		double tC = tAct + tau_reduce - tauW - tauC;
		Line line = new Line();
		if (fridge.isActive()) {
			line.x1 = fridge.tau(fridge.getT_current(), fridge.getT_min(),
					fridge.getQ_cooling());
			line.y1 = fridge.getT_min();
			line.x2 = line.x1 + fridge.tauWarming(fridge.getQ_warming());
			line.y2 = fridge.getT_max();
		} else {
			line.x1 = 0.0;
			line.y1 = fridge.getT_current();
			line.x2 = line.x1
					+ fridge.tau(line.y1, fridge.getT_max(), fridge
							.getQ_warming());
			line.y2 = fridge.getT_max();
		}
		Line s = new Line();
		s.x1 = tC - now;
		s.y1 = fridge.getT_max();
		s.x2 = s.x1 + fridge.tauCooling(fridge.getQ_cooling());
		s.y2 = fridge.getT_min();
		double[] is = new double[2];
		int ret = Geometry.findLineSegmentIntersection(line.x1, line.y1,
				line.x2, line.y2, s.x1, s.y1, s.x2, s.y2, is);
		if (ret == 1) {
			// If an intersection has occured, schedule the classification again
			// at that point in time.
			double delay = is[0] - now;
			if (delay >= 0.0) {
				waitDelay(EV_REDUCE_LOAD, is[0], tau_preload - is[0],
						tau_reduce);
				return 0;
			} else {
				// This should not happen.
				System.err.println(getName()
						+ " - intersection with sDC is in negative time!");
				return -2;
			}
		} else {
			// No intersection found.
			return -1;
		}
	}
	
	protected int intersectSCB(double tau_preload, double tau_reduce) {
		double now = getEventList().getSimTime();
		double tAct = now + tau_preload;
		double tauC = fridge.tauCooling(fridge.getQ_cooling());
		double tB = tAct - tauC;
		if (now < tB) {
			// The line segment sCB has not been reached. Reschedule this
			// when we reach tB.
			waitDelay(EV_REDUCE_LOAD, tB - now, tauC, tau_reduce);
			return 0;
		} else {
			double ac = ((fridge.getT_min() - fridge.getT_max()) / fridge
					.tauCooling(fridge.getQ_cooling()));
			double TAllowed = fridge.getT_max() + (ac * (now - tB));
			double TCurrent = fridge.getT_current();
			if (TCurrent > TAllowed) {
				// We are too warm and cannot reach Tmin. Start cooling
				// immediately till tAct.
				waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
						.calculateTemperatureAfter(true, tau_preload, TCurrent,
								fridge.getQ_cooling()), fridge.getQ_cooling());
				return 0;
			} else {
				// Calculate intersection of current curve and sCB
				Line s = new Line();
				s.x1 = tB;
				s.y1 = fridge.getT_max();
				s.x2 = tAct;
				s.y2 = fridge.getT_min();
				Line c = new Line();
				if (fridge.isActive()) {
					c.x1 = now
							+ fridge.tau(fridge.getT_current(), fridge
									.getT_min(), fridge.getQ_cooling());
					c.y1 = fridge.getT_min();
					c.x2 = c.x1 + fridge.tauWarming(fridge.getQ_warming());
					c.y2 = fridge.getT_max();
				} else {
					c.x1 = now;
					c.y1 = fridge.getT_current();
					c.x2 = c.x1
							+ fridge.tau(c.y1, fridge.getT_max(), fridge
									.getQ_warming());
					c.y2 = fridge.getT_max();
				}
				double[] is = new double[2];
				int ret = Geometry.findLineSegmentIntersection(c.x1, c.y1,
						c.x2, c.y2, s.x1, s.y1, s.x2, s.y2, is);
				if (ret == 1) {
					double delay = is[0] - now;
					if (delay >= 0.0) {
						// Schedule cooling program at time of intersection
						waitDelay(EV_DEL_AND_TARGET_TO, delay, fridge
								.getT_min(), fridge.getQ_cooling());
						return 0;
					} else {
						// This should not happen.
						System.err
								.println(getName()
										+ " - intersection with sCB is in negative time!");
						return -2;
					}
				} else {
					// No intersection found.
					return -1;
				}
			}
		}
	}

	protected int intersectSBA(double tau_preload, double tau_reduce) {
		double now = getEventList().getSimTime();
		double tAct = now + tau_preload;
		double tauW = fridge.tauWarming(fridge.getQ_warming());
		double tC1 = tAct + tau_reduce - tauW;
		double aw = (fridge.getT_max() - fridge.getT_min())
				/ fridge.tauWarming(fridge.getQ_warming());
		double T_maxact = fridge.getT_min() + (aw * (tAct - tC1));
		double ac = ((fridge.getT_min() - fridge.getT_max()) / fridge
				.tauCooling(fridge.getQ_cooling()));
		double tA = tAct - ((T_maxact - fridge.getT_max()) / ac);
		if (now < tA) {
			// The line segment sBA has not been reached.
			// Reschedule this when we reach tA.
			double delay = tA - now;
			waitDelay(EV_REDUCE_LOAD, delay, tau_preload - delay, tau_reduce);
			return 0;
		} else {
			double TAllowed = fridge.getT_max() + (ac * (now - tA));
			double TCurrent = fridge.getT_current();
			if (TCurrent > TAllowed) {
				// Temperature too high, we cannot reach
				// T_maxact! Start cooldown till tAct to
				// get the best possible result.
				waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
						.calculateTemperatureAfter(true, tau_preload, TCurrent,
								fridge.getQ_cooling()), fridge.getQ_cooling());
				return 0;
			} else {
				// Find intersection with sBA
				Line s = new Line();
				s.x1 = tA;
				s.y1 = fridge.getT_max();
				s.x2 = tAct;
				s.y2 = T_maxact;
				Line c = new Line();
				if (fridge.isActive()) {
					c.x1 = now
							+ fridge.tau(fridge.getT_current(), fridge
									.getT_min(), fridge.getQ_cooling());
					c.y1 = fridge.getT_min();
					c.x2 = c.x1 + fridge.tauWarming(fridge.getQ_warming());
					c.y2 = fridge.getT_max();
				} else {
					c.x1 = now;
					c.y1 = fridge.getT_current();
					c.x2 = c.x1
							+ fridge.tau(c.y1, fridge.getT_max(), fridge
									.getQ_warming());
					c.y2 = fridge.getT_max();
				}
				double[] is = new double[2];
				int ret = Geometry.findLineSegmentIntersection(c.x1, c.y1,
						c.x2, c.y2, s.x1, s.y1, s.x2, s.y2, is);
				if (ret == 1) {
					double delay = is[0] - now;
					if (delay >= 0.0) {
						// Schedule cooling program at time of
						// intersection, and cool down to T_max-act
						waitDelay(EV_DEL_AND_TARGET_TO, delay, T_maxact, fridge
								.getQ_cooling());
						return 0;
					} else {
						// This should not happen.
						System.err
								.println(getName()
										+ " - intersection with sBA is in negative time!");
						return -2;
					}
				} else {
					// No intersection found.
					return -1;
				}
			}
		}
	}

	protected int intersectSCC1(double tau_preload, double tau_reduce) {
		double now = getEventList().getSimTime();
		double tAct = now + tau_preload;
		double tauW = fridge.tauWarming(fridge.getQ_warming());
		double tC1 = tAct + tau_reduce - tauW;
		double aw = (fridge.getT_max() - fridge.getT_min())
				/ fridge.tauWarming(fridge.getQ_warming());
		double T_maxact = fridge.getT_min() + (aw * (tAct - tC1));
		if (now < tC1) {
			// The line segment sCC1 has not been reached. Reschedule this
			// when we reach tC1.
			double delay = tC1 - now;
			waitDelay(EV_REDUCE_LOAD, delay, tau_preload - delay, tau_reduce);
			return 0;
		} else {
			double TAllowed = fridge.getT_min() + (aw * (now - tC1));
			double TCurrent = fridge.getT_current();
			if (TCurrent < TAllowed) {
				// We are too cold. This not really a problem, but we can
				// switch to 'warming' to reduce the overcooling.
				waitDelay(EV_DEL_AND_TARGET_TO, 0.0, fridge
						.getT_max(), fridge.getQ_warming());
				return 0;
			} else {
				// Find intersection with sCC1
				Line s = new Line();
				s.x1 = tC1;
				s.y1 = fridge.getT_min();
				s.x2 = tAct;
				s.y2 = T_maxact;
				Line c = new Line();
				if (!fridge.isActive()) {
					c.x1 = now
							+ fridge.tau(fridge.getT_current(), fridge
									.getT_max(), fridge.getQ_warming());
					c.y1 = fridge.getT_max();
					c.x2 = c.x1 + fridge.tauCooling(fridge.getQ_cooling());
					c.y2 = fridge.getT_min();
				} else {
					c.x1 = now;
					c.y1 = fridge.getT_current();
					c.x2 = c.x1
							+ fridge.tau(c.y1, fridge.getT_min(), fridge
									.getQ_cooling());
					c.y2 = fridge.getT_min();
				}
				double[] is = new double[2];
				int ret = Geometry.findLineSegmentIntersection(c.x1, c.y1,
						c.x2, c.y2, s.x1, s.y1, s.x2, s.y2, is);
				if (ret == 1) {
					double delay = is[0] - now;
					if (delay >= 0.0) {
						// Schedule stop of cooling at time of intersection
						waitDelay(EV_DEL_AND_TARGET_TO, delay, fridge
								.getT_max(), fridge.getQ_warming());
						return 0;
					} else {
						// This should not happen.
						System.err
								.println(getName()
										+ " - intersection with sCC1 is in negative time!");
						return -2;
					}
				} else {
					// No intersection found.
					return -1;
				}
			}
		}
	}
	
	public void doDeleteAndTargetTo(Double t_dest, Double load) {
		// First perform cancelling edge
		interruptAll(EV_TARGET_TO);
		// Then immediately perform requested operation
		waitDelay(EV_TARGET_TO, 0.0, t_dest, load);
	}

	/**
	 * Helper class which represents a line segment.
	 * 
	 * @author Chh
	 */
	protected class Line {
		public double x1;
		public double y1;
		public double x2;
		public double y2;
		public Line(){}
	}
}