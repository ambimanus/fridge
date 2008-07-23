package de.uniol.ui.desync.model.fridges;

import java.text.NumberFormat;

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
	
	protected double t_current = Double.NaN;
	protected double q_current = Double.NaN;
	protected boolean active;

	public State() {
	}

	public State(double t_current, double q_current, boolean active) {
		this.t_current = t_current;
		this.q_current = q_current;
		this.active = active;
	}

	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(getClass().getSimpleName());
		out.append('(');
		out.append("t_current=");
		out.append(nfT.format(t_current));
		out.append(", q_current=");
		out.append(nfL.format(q_current));
		out.append(", active=");
		out.append(active);
		out.append(')');
		return out.toString();
	}
}