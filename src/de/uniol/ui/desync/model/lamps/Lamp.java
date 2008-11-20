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
package de.uniol.ui.desync.model.lamps;

import simkit.random.Congruential;
import simkit.random.UniformVariate;
import de.uniol.ui.desync.model.SimEntityClean;
import de.uniol.ui.desync.model.fridges.AbstractFridge;

/**
 * This class represents a lamp of a fridge as SimEntity.
 *
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 *
 */
public class Lamp extends SimEntityClean {

	/* event identifiers */
	public static String EV_SWITCH_ON = "SwitchOn";
	public static String EV_SWITCH_OFF = "SwitchOff";
	
	/* some fields */
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
	
	/** length of a lighting phase */
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