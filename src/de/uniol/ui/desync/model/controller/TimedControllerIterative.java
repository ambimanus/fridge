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
package de.uniol.ui.desync.model.controller;

import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.signals.Itlr;
import de.uniol.ui.desync.util.Geometry;

/**
 * This controller extension adds the TLR control mode to a
 * {@link BaseControllerIterative}.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 * @deprecated NOT FINISHED YET! Copy code from
 *             {@link TimedControllerCompactLinear} to finish.
 * 
 */
public class TimedControllerIterative extends BaseControllerIterative implements
		Itlr {
	
	public TimedControllerIterative(IterativeFridge fridge, int eventListID) {
		super(fridge, eventListID);
	}
	
	public void doReduceLoad(Double tau_preload, Double tau_reduce) {
		strategyCoolFitting2(tau_preload, tau_reduce);
		// FIXME not working yet!!
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
			// To do this, we have to check an intersecion with sCC1 or sBA. We
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
				waitDelay(EV_BEGIN_COOLING, 0.0, fridge
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
						waitDelay(EV_BEGIN_COOLING, delay, fridge.getT_min(),
								fridge.getQ_cooling());
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
				waitDelay(EV_BEGIN_COOLING, 0.0, fridge
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
						waitDelay(EV_BEGIN_COOLING, delay, T_maxact, fridge
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
				waitDelay(EV_BEGIN_WARMING, 0.0, fridge.getT_max(), fridge
						.getQ_warming());
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
						waitDelay(EV_BEGIN_WARMING, delay, fridge.getT_max(),
								fridge.getQ_warming());
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

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginCooling(java.lang.Double)
	 */
	@Override
	public void doBeginCooling(Double load) {
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		super.doBeginCooling(load);
	}

	/* (non-Javadoc)
	 * @see de.uniol.ui.desync.model.controller.BaseControllerIterative#doBeginWarming(java.lang.Double)
	 */
	@Override
	public void doBeginWarming(Double load) {
		interruptAll(EV_COOLING);
		interruptAll(EV_WARMING);
		super.doBeginWarming(load);
	}
}