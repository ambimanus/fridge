package de.uniol.ui.desync.model.fridges;

import java.text.NumberFormat;

/**
 * Represents a fridge's state consisting of the tupel (temperature, load,
 * phase).
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class State {

	private final static NumberFormat nfT = NumberFormat.getInstance();
	private final static NumberFormat nfL = NumberFormat.getInstance();
	static {
		nfT.setMinimumFractionDigits(4);
		nfT.setMaximumFractionDigits(4);
		nfT.setMinimumIntegerDigits(1);
		nfT.setMaximumIntegerDigits(1);
		nfL.setMinimumFractionDigits(4);
		nfL.setMaximumFractionDigits(4);
		nfL.setMinimumIntegerDigits(2);
		nfL.setMaximumIntegerDigits(2);
	}
	
	public double t = Double.NaN;
	public double q = Double.NaN;
	public boolean active;

	public State() {
	}

	public State(double t, double q, boolean active) {
		this.t = t;
		this.q = q;
		this.active = active;
	}
	
	public State(AbstractFridge fridge) {
		this.t = fridge.getT_current();
		this.q = fridge.getLoad();
		this.active = fridge.isActive();
	}

	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(getClass().getSimpleName());
		out.append('(');
		out.append("t=");
		out.append(nfT.format(t));
		out.append(", q=");
		out.append(nfL.format(q));
		out.append(", active=");
		out.append(active);
		out.append(')');
		return out.toString();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof State) {
			State casted = (State) obj;
			if (casted.t == t && casted.q == q && casted.active == active) {
				return true;
			}
		}
		return false;
	}
}