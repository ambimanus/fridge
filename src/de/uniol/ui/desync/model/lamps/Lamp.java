package de.uniol.ui.desync.model.lamps;

import simkit.random.Congruential;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

public class Lamp extends SimEntityClean {

	public static String EV_SWITCH_ON = "SwitchOn";
	public static String EV_SWITCH_OFF = "SwitchOff";
	
	protected static int instance = -1;
	protected static double activationsPerDay = 11.07;
	protected static double stddev = 4.81;
	protected static double q_min = 5.0;
	protected static double q_max = 25.0;
	protected static UniformVariate delayVariate = new UniformVariate();
	protected static UniformVariate loadVariate = new UniformVariate();
	static {
		Congruential cong = new Congruential();
		cong.setSeed(System.currentTimeMillis());
		delayVariate.setRandomNumber(cong);
		delayVariate.setMinimum((18.0 * 60.0) / (activationsPerDay - stddev));
		delayVariate.setMaximum((18.0 * 60.0) / (activationsPerDay + stddev));
		
		cong = new Congruential();
		cong.setSeed(System.currentTimeMillis() / 2l);
		loadVariate.setRandomNumber(cong);
		loadVariate.setMinimum(q_min);
		loadVariate.setMaximum(q_max);
	}
	
	protected double blockSize = 5.0 / 60.0;
	protected UniformVariate firstSwitchVariate;
	
	protected AbstractFridge fridge;
	
	public Lamp(int eventListID, AbstractFridge fridge) {
		super("Lamp" + (++instance), eventListID);
		this.fridge = fridge;
		firstSwitchVariate = new UniformVariate();
		Congruential cong = new Congruential();
		cong.setSeed(System.currentTimeMillis() + (long) (instance));
		firstSwitchVariate.setRandomNumber(cong);
	}
	
	public void doRun() {
		fridge.setExtraLoad(0.0);
		waitDelay(EV_SWITCH_ON, firstSwitchVariate.generate());
	}
	
	public void doSwitchOn() {
		fridge.setExtraLoad(loadVariate.generate());
		waitDelay(EV_SWITCH_OFF, blockSize);
	}
	
	public void doSwitchOff() {
		fridge.setExtraLoad(0.0);
		waitDelay(EV_SWITCH_ON, delayVariate.generate());
	}
}